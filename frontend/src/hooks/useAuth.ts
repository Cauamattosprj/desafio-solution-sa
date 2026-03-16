import { useCallback, useState } from 'react'
import type { AuthInfo } from '../types'
import { setAuthToken as setServiceAuthToken } from '../services'

type UseAuthResult = {
  auth: AuthInfo | null
  setAuth: (auth: AuthInfo | null) => void
  logout: () => void
}

export function useAuth(initialAuth: AuthInfo | null = null): UseAuthResult {
  const [auth, setAuthState] = useState<AuthInfo | null>(initialAuth)

  const setAuth = useCallback((nextAuth: AuthInfo | null) => {
    if (nextAuth?.token) {
      setServiceAuthToken(nextAuth.token)
    } else {
      setServiceAuthToken(null)
    }
    setAuthState(nextAuth)
  }, [])

  const logout = useCallback(() => {
    setAuth(null)
  }, [setAuth])

  return { auth, setAuth, logout }
}
