import 'dart:math' as math;

import 'package:flutter/material.dart';

import '../../core/theme/app_theme.dart';
import '../../core/theme/design_tokens.dart';
import '../../core/widgets/kisan_components.dart';
import '../../data/models/kisan_models.dart';
import '../../data/services/kisan_voice_service.dart';

/// Voice AI Screen — Minimalist Urdu Agronomy Voice Companion.
class AskScreen extends StatefulWidget {
  const AskScreen({
    super.key,
    required this.voiceService,
  });

  final IKisanVoiceService voiceService;

  @override
  State<AskScreen> createState() => _AskScreenState();
}

class _AskScreenState extends State<AskScreen>
    with SingleTickerProviderStateMixin {
  late final AnimationController _waveController;
  final TextEditingController _textController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  bool _isListening = false;
  bool _isProcessing = false;
  String? _speakingMessageId;

  late final List<VoicePromptSuggestion> _prompts;
  final List<VoiceConversationEntry> _messages = <VoiceConversationEntry>[
    const VoiceConversationEntry(
      id: 'welcome_msg',
      isFarmer: false,
      textUr:
          'السلام علیکم راشد بھائی! مائیک کا بٹن دبا کر بوائی، وتر، کھاد یا فصل کی نگہداشت کے بارے میں اپنا سوال اردو یا پوٹھوہاری میں بولیں۔',
      summaryEn:
          'Tap the microphone to ask about soil wattar, sowing, fertilizer timing, or pest scouting.',
      timestampLabel: 'Ready · Pothohar Agronomy AI',
      sources: <String>['SMAP Live', 'BARI Chakwal'],
    ),
  ];

  @override
  void initState() {
    super.initState();
    _prompts = widget.voiceService.getQuickPrompts();
    _waveController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1800),
    );
  }

  @override
  void dispose() {
    _waveController.dispose();
    _textController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _toggleVoiceCapture() async {
    if (_isProcessing) return;

    if (!_isListening) {
      setState(() => _isListening = true);
      _waveController.repeat();
      return;
    }

    // Stop listening -> Transcribe -> Fetch AI response
    _waveController.stop();
    _waveController.reset();
    setState(() {
      _isListening = false;
      _isProcessing = true;
    });

    final String transcriptUr =
        await widget.voiceService.simulateOrTranscribeVoice();
    await _submitFarmerQuery(transcriptUr, isVoice: true);
  }

  Future<void> _submitFarmerQuery(
    String queryText, {
    bool isVoice = false,
  }) async {
    final String trimmed = queryText.trim();
    if (trimmed.isEmpty) {
      setState(() => _isProcessing = false);
      return;
    }

    _textController.clear();
    setState(() {
      _isProcessing = true;
      _messages.add(
        VoiceConversationEntry(
          id: 'farmer_${DateTime.now().millisecondsSinceEpoch}',
          isFarmer: true,
          textUr: trimmed,
          summaryEn: isVoice ? 'Voice transcription · Urdu' : 'Typed question',
          timestampLabel: 'Just now',
          confidencePct: isVoice ? 98 : null,
        ),
      );
    });
    _scrollToBottom();

    final VoiceConversationEntry reply =
        await widget.voiceService.askAgronomyAssistant(queryUr: trimmed);
    if (!mounted) return;

    final String replyId = 'ai_${DateTime.now().millisecondsSinceEpoch}';
    setState(() {
      _isProcessing = false;
      _messages.add(
        VoiceConversationEntry(
          id: replyId,
          isFarmer: false,
          textUr: reply.textUr,
          summaryEn: reply.summaryEn,
          timestampLabel: reply.timestampLabel,
          sources: reply.sources,
        ),
      );
      _speakingMessageId = replyId;
    });
    _scrollToBottom();
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent + 120,
          duration: const Duration(milliseconds: 280),
          curve: Curves.easeOutCubic,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    return Column(
      children: <Widget>[
        // 1. Subtle Field Context Strip
        Container(
          width: double.infinity,
          padding: const EdgeInsets.symmetric(
            horizontal: DesignTokens.space16,
            vertical: DesignTokens.space8,
          ),
          decoration: BoxDecoration(
            color: colors.cardSurface,
            border: Border(
              bottom: BorderSide(
                color: colors.border,
                width: DesignTokens.borderWidth,
              ),
            ),
          ),
          child: Row(
            children: <Widget>[
              Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(
                  color: colors.brandPrimary,
                  shape: BoxShape.circle,
                ),
              ),
              const SizedBox(width: DesignTokens.space8),
              Expanded(
                child: Text(
                  'Context: Bara Khet (4.0 Ac) · SMAP 24% Wattar · Akbar-19',
                  overflow: TextOverflow.ellipsis,
                  style: KisanTypography.microLabel(color: colors.textSecondary)
                      .copyWith(letterSpacing: 0.2),
                ),
              ),
            ],
          ),
        ),

        // 2. Premium Messaging Transcript & Response Feed
        Expanded(
          child: ListView.separated(
            controller: _scrollController,
            padding: const EdgeInsets.all(DesignTokens.space16),
            itemCount: _messages.length + (_isProcessing ? 1 : 0),
            separatorBuilder: (_, __) =>
                const SizedBox(height: DesignTokens.space12),
            itemBuilder: (BuildContext context, int index) {
              if (index == _messages.length) {
                return const _ThinkingMessageCard();
              }
              final VoiceConversationEntry entry = _messages[index];
              return _ConversationMessageCard(
                entry: entry,
                isSpeaking: _speakingMessageId == entry.id,
                onToggleSpeak: () {
                  setState(() {
                    _speakingMessageId =
                        _speakingMessageId == entry.id ? null : entry.id;
                  });
                },
              );
            },
          ),
        ),

        // 3. Bottom Dock: Quick Prompt Chips + Central Mic + Optional Text Input
        Container(
          decoration: BoxDecoration(
            color: colors.cardSurface,
            border: Border(
              top: BorderSide(
                color: colors.border,
                width: DesignTokens.borderWidth,
              ),
            ),
          ),
          padding: const EdgeInsets.only(
            top: DesignTokens.space12,
            bottom: DesignTokens.space16,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              // Quick Voice Prompt Suggestions (Horizontal Chips)
              SizedBox(
                height: 54,
                child: ListView.separated(
                  scrollDirection: Axis.horizontal,
                  padding: const EdgeInsets.symmetric(
                    horizontal: DesignTokens.space16,
                  ),
                  itemCount: _prompts.length,
                  separatorBuilder: (_, __) =>
                      const SizedBox(width: DesignTokens.space8),
                  itemBuilder: (BuildContext context, int index) {
                    final VoicePromptSuggestion prompt = _prompts[index];
                    return KisanChip(
                      label: prompt.labelUr,
                      subtitle: prompt.labelEn,
                      isUrdu: true,
                      leadingIcon: Icons.mic_none_rounded,
                      onTap: () => _submitFarmerQuery(
                        prompt.queryUr,
                        isVoice: false,
                      ),
                    );
                  },
                ),
              ),

              const SizedBox(height: DesignTokens.space12),

              // Central Deep Forest Green Microphone + Waveform Ripple Canvas
              SizedBox(
                height: 132,
                child: Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      SizedBox(
                        width: 168,
                        height: 96,
                        child: AnimatedBuilder(
                          animation: _waveController,
                          builder: (BuildContext context, Widget? child) {
                            return CustomPaint(
                              painter: _VoiceWaveformRipplePainter(
                                progress: _waveController.value,
                                isActive: _isListening,
                                primaryColor: DesignTokens.forestGreen,
                                idleRingColor: colors.border,
                              ),
                              child: child,
                            );
                          },
                          child: Center(
                            child: Semantics(
                              button: true,
                              label: _isListening
                                  ? 'Stop recording and send voice question'
                                  : 'Start recording voice question in Urdu',
                              child: Material(
                                color: _isListening
                                    ? DesignTokens.forestGreenDark
                                    : DesignTokens.forestGreen,
                                shape: const CircleBorder(),
                                clipBehavior: Clip.antiAlias,
                                elevation: DesignTokens.elevationSubtle,
                                child: InkWell(
                                  onTap: _toggleVoiceCapture,
                                  child: SizedBox(
                                    width: DesignTokens.voiceMicButtonSize,
                                    height: DesignTokens.voiceMicButtonSize,
                                    child: Icon(
                                      _isListening
                                          ? Icons.stop_rounded
                                          : Icons.mic_rounded,
                                      size: 34,
                                      color: Colors.white,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(height: DesignTokens.space4),
                      Text(
                        _isListening
                            ? 'سن رہا ہے... روکنے کے لیے دبائیں · Tap to Send'
                            : _isProcessing
                                ? 'تجزیہ جاری ہے... · Analyzing'
                                : 'بولنے کے لیے مائیک دبائیں · Tap to Speak',
                        style: KisanTypography.bodyMedium(
                          color: _isListening
                              ? colors.brandPrimary
                              : colors.textSecondary,
                        ).copyWith(fontSize: 12.5),
                      ),
                    ],
                  ),
                ),
              ),

              // Subtle Secondary Text Input Bar
              Padding(
                padding: const EdgeInsets.fromLTRB(
                  DesignTokens.space16,
                  DesignTokens.space8,
                  DesignTokens.space16,
                  0,
                ),
                child: Row(
                  children: <Widget>[
                    Expanded(
                      child: ConstrainedBox(
                        constraints: const BoxConstraints(
                          minHeight: DesignTokens.minTouchTarget,
                        ),
                        child: TextField(
                          controller: _textController,
                          textDirection: TextDirection.rtl,
                          onSubmitted: (String val) => _submitFarmerQuery(val),
                          style: KisanTypography.urduBody(
                            color: colors.textPrimary,
                            fontSize: 14,
                          ),
                          decoration: InputDecoration(
                            hintText: 'یا یہاں اپنا سوال لکھیں... (Type question)',
                            hintTextDirection: TextDirection.rtl,
                            hintStyle: KisanTypography.body(
                              color: colors.textTertiary,
                            ).copyWith(fontSize: 13),
                            filled: true,
                            fillColor: colors.subtleSurface,
                            contentPadding: const EdgeInsets.symmetric(
                              horizontal: DesignTokens.space16,
                              vertical: DesignTokens.space12,
                            ),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(
                                DesignTokens.radiusSm,
                              ),
                              borderSide: BorderSide(color: colors.border),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(
                                DesignTokens.radiusSm,
                              ),
                              borderSide: BorderSide(
                                color: colors.brandPrimary,
                                width: DesignTokens.borderWidthEmphasis,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: DesignTokens.space8),
                    KisanIconAction(
                      icon: Icons.arrow_upward_rounded,
                      tooltip: 'Send message',
                      isActive: true,
                      onTap: () => _submitFarmerQuery(_textController.text),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class _ConversationMessageCard extends StatelessWidget {
  const _ConversationMessageCard({
    required this.entry,
    required this.isSpeaking,
    required this.onToggleSpeak,
  });

  final VoiceConversationEntry entry;
  final bool isSpeaking;
  final VoidCallback onToggleSpeak;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    if (entry.isFarmer) {
      return Padding(
        padding: const EdgeInsets.only(left: DesignTokens.space32),
        child: KisanCard(
          backgroundColor: colors.subtleSurface,
          borderColor: colors.borderStrong,
          padding: const EdgeInsets.all(DesignTokens.space16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Row(
                    children: <Widget>[
                      Icon(
                        Icons.graphic_eq_rounded,
                        size: 14,
                        color: colors.brandPrimary,
                      ),
                      const SizedBox(width: DesignTokens.space4),
                      Text(
                        entry.summaryEn.toUpperCase(),
                        style: KisanTypography.microLabel(
                          color: colors.textSecondary,
                        ).copyWith(fontSize: 10),
                      ),
                    ],
                  ),
                  if (entry.confidencePct != null)
                    Text(
                      '${entry.confidencePct}% ASR',
                      style: KisanTypography.microLabel(
                        color: colors.brandPrimary,
                      ).copyWith(fontSize: 10),
                    ),
                ],
              ),
              const SizedBox(height: DesignTokens.space8),
              UrduText(
                entry.textUr,
                style: KisanTypography.urduBody(
                  color: colors.textPrimary,
                  fontSize: 15.5,
                ),
              ),
            ],
          ),
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.only(right: DesignTokens.space16),
      child: KisanCard(
        padding: const EdgeInsets.all(DesignTokens.space16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            Row(
              children: <Widget>[
                Container(
                  width: 24,
                  height: 24,
                  decoration: BoxDecoration(
                    color: colors.brandSubtle,
                    borderRadius: BorderRadius.circular(DesignTokens.radiusXs),
                    border: Border.all(
                      color: colors.brandBorder,
                      width: DesignTokens.borderWidth,
                    ),
                  ),
                  child: Icon(
                    Icons.eco_outlined,
                    size: 14,
                    color: colors.brandPrimary,
                  ),
                ),
                const SizedBox(width: DesignTokens.space8),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(
                        'KISAN SAATHI',
                        style: KisanTypography.microLabel(
                          color: colors.textPrimary,
                        ),
                      ),
                      Text(
                        entry.timestampLabel,
                        style: KisanTypography.microLabel(
                          color: colors.textSecondary,
                        ).copyWith(fontSize: 10, letterSpacing: 0.1),
                      ),
                    ],
                  ),
                ),
                KisanIconAction(
                  icon: isSpeaking
                      ? Icons.pause_circle_outline_rounded
                      : Icons.volume_up_outlined,
                  tooltip: isSpeaking ? 'Pause voice playback' : 'Play Urdu audio',
                  isActive: isSpeaking,
                  onTap: onToggleSpeak,
                ),
              ],
            ),
            const SizedBox(height: DesignTokens.space12),
            UrduText(
              entry.textUr,
              style: KisanTypography.urduBody(
                color: colors.textPrimary,
                fontSize: 15.5,
              ),
            ),
            const SizedBox(height: DesignTokens.space8),
            Text(
              entry.summaryEn,
              style: KisanTypography.body(color: colors.textSecondary)
                  .copyWith(fontSize: 13),
            ),
            if (entry.sources.isNotEmpty) ...<Widget>[
              const SizedBox(height: DesignTokens.space12),
              Wrap(
                spacing: DesignTokens.space8,
                runSpacing: DesignTokens.space4,
                children: entry.sources.map((String s) {
                  return Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: DesignTokens.space8,
                      vertical: DesignTokens.space4,
                    ),
                    decoration: BoxDecoration(
                      color: colors.subtleSurface,
                      borderRadius: BorderRadius.circular(DesignTokens.radiusXs),
                    ),
                    child: Text(
                      s,
                      style: KisanTypography.microLabel(
                        color: colors.textSecondary,
                      ).copyWith(fontSize: 10, letterSpacing: 0.2),
                    ),
                  );
                }).toList(),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _ThinkingMessageCard extends StatelessWidget {
  const _ThinkingMessageCard();

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return KisanCard(
      padding: const EdgeInsets.all(DesignTokens.space16),
      child: Row(
        children: <Widget>[
          SizedBox(
            width: 16,
            height: 16,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              color: colors.brandPrimary,
            ),
          ),
          const SizedBox(width: DesignTokens.space12),
          Expanded(
            child: UrduText(
              'زرعی ڈیٹا اور کھیت کے وتر کی روشنی میں جواب تیار کیا جا رہا ہے...',
              textAlign: TextAlign.left,
              style: KisanTypography.urduBody(
                color: colors.textSecondary,
                fontSize: 14,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

/// Custom Canvas painter rendering calm concentric ripples and bilateral
/// acoustic waveform bars around the central Forest Green microphone button.
class _VoiceWaveformRipplePainter extends CustomPainter {
  const _VoiceWaveformRipplePainter({
    required this.progress,
    required this.isActive,
    required this.primaryColor,
    required this.idleRingColor,
  });

  final double progress;
  final bool isActive;
  final Color primaryColor;
  final Color idleRingColor;

  @override
  void paint(Canvas canvas, Size size) {
    final Offset center = Offset(size.width / 2, size.height / 2);
    const double baseRadius = DesignTokens.voiceMicButtonSize / 2;

    if (!isActive) {
      final Paint idlePaint = Paint()
        ..color = idleRingColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.0;
      canvas.drawCircle(center, baseRadius + 6, idlePaint);
      return;
    }

    // 1. Two staggered concentric outward ripples
    for (int i = 0; i < 2; i++) {
      final double phase = (progress + i * 0.5) % 1.0;
      final double radius = baseRadius + 4 + (phase * 26);
      final double opacity = (1.0 - phase) * 0.28;

      final Paint ripplePaint = Paint()
        ..color = primaryColor.withValues(alpha: opacity)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.8;
      canvas.drawCircle(center, radius, ripplePaint);
    }

    // 2. Crisp bilateral acoustic bars flanking the microphone button
    final Paint barPaint = Paint()
      ..color = primaryColor.withValues(alpha: 0.72)
      ..strokeCap = StrokeCap.round
      ..strokeWidth = 3.0;

    const int barsPerSide = 4;
    const double barSpacing = 7.5;
    for (int side in const <int>[-1, 1]) {
      for (int b = 0; b < barsPerSide; b++) {
        final double x =
            center.dx + side * (baseRadius + 16 + (b * barSpacing));
        final double wave = math.sin(
          (progress * 2 * math.pi * 2) + (b * 0.85),
        );
        final double halfHeight =
            4.0 + ((wave + 1.0) * 0.5) * (14.0 - b * 2.2);

        canvas.drawLine(
          Offset(x, center.dy - halfHeight),
          Offset(x, center.dy + halfHeight),
          barPaint,
        );
      }
    }
  }

  @override
  bool shouldRepaint(covariant _VoiceWaveformRipplePainter oldDelegate) {
    return oldDelegate.progress != progress ||
        oldDelegate.isActive != isActive ||
        oldDelegate.idleRingColor != idleRingColor;
  }
}
