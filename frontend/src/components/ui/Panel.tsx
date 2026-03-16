import React from 'react'

export function Panel({ children }: { children: React.ReactNode }) {
  return <section className="shacn-card">{children}</section>
}

export function Toast({ message, type }: { message: string; type: 'success' | 'error' }) {
  if (!message) return null

  const className = type === 'success' ? 'bg-green-500/15 text-green-700 border border-green-300' : 'bg-red-500/15 text-red-800 border border-red-300'

  return <div className={`rounded-md px-4 py-2 text-sm ${className}`}>{message}</div>
}
