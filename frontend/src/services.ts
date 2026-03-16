import type { Address, User } from './types'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
let authToken: string | null = null

export function setAuthToken(token: string | null) {
  authToken = token
}

async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }

  if (authToken) {
    headers.Authorization = authToken
  }

  const resp = await fetch(`${API_BASE}${url}`, {
    headers,
    ...options,
  })
  if (!resp.ok) {
    const text = await resp.text()
    throw new Error(text || resp.statusText)
  }

  // Some endpoints like DELETE return 204 No Content
  if (resp.status === 204) {
    return undefined as unknown as T
  }

  const text = await resp.text()
  if (!text) {
    return undefined as unknown as T
  }
  return JSON.parse(text) as T
}

export type LoginPayload = { cpf: string; password: string }
export type RegisterPayload = { name: string; cpf: string; password: string; role: 'ADMIN' | 'USER' }
export type AddressPayload = { cep: string; number: string; complement?: string; street: string; neighborhood: string; city: string; state: string; main: boolean }

export async function login(payload: LoginPayload): Promise<User> {
  return request<User>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function register(payload: RegisterPayload): Promise<User> {
  return request<User>('/auth/user', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function fetchUsers(): Promise<User[]> {
  return request<User[]>('/users')
}

export async function fetchUser(userId: string): Promise<User> {
  return request<User>(`/users/${userId}`)
}

export async function fetchAddresses(userId: string): Promise<Address[]> {
  return request<Address[]>(`/users/${userId}/addresses`)
}

export async function createAddress(userId: string, payload: AddressPayload): Promise<Address> {
  return request<Address>(`/users/${userId}/addresses`, { method: 'POST', body: JSON.stringify(payload) })
}

export async function updateAddress(userId: string, addressId: string, payload: AddressPayload): Promise<Address> {
  return request<Address>(`/users/${userId}/addresses/${addressId}`, { method: 'PUT', body: JSON.stringify(payload) })
}

export async function deleteAddress(userId: string, addressId: string): Promise<void> {
  await request<void>(`/users/${userId}/addresses/${addressId}`, { method: 'DELETE' })
}

export async function fetchCep(cep: string): Promise<{ cep: string; logradouro: string; bairro: string; localidade: string; uf: string }> {
  return request(`/viacep/${cep}`)
}
