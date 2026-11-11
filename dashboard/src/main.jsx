import React from 'react'
import { createRoot } from 'react-dom/client'

const App = () => (
  <main style={{ fontFamily: 'system-ui', padding: 24 }}>
    <h1>Kisan Saathi Partner Dashboard</h1>
    <p>Barkat totals are shown as estimates (اندازہ).</p>
  </main>
)

createRoot(document.getElementById('root')).render(<App />)
