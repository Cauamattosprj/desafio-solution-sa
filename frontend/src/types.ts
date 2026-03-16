export type UserRole = 'ADMIN' | 'USER'

export type User = {
  id: string
  name: string
  cpf: string
  role: UserRole
}

export type Address = {
  id: string
  cep: string
  number: string
  complement?: string
  street: string
  neighborhood: string
  city: string
  state: string
  main: boolean
}

export type AuthInfo = {
  userId: string
  name: string
  role: UserRole
  cpf: string
  token?: string
}
