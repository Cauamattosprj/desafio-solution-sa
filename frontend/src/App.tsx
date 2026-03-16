import {
  createBrowserHistory,
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
  Outlet,
  useLocation,
  useNavigate,
  Navigate,
  getRouteApi,
} from '@tanstack/react-router'
import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import './App.css'
import { LoginForm } from './components/login-form'
import { RegisterForm } from './components/auth/RegisterForm'
import { AddressForm } from './components/dashboard/AddressForm'
import { AddressPanel } from './components/dashboard/AddressPanel'
import { Toast } from './components/ui/Panel'
import { Button } from './components/ui/button'
import type { Address, AuthInfo, User } from './types'
import { fetchAddresses, fetchCep, fetchUsers, login, register, createAddress, updateAddress, deleteAddress } from './services'
import { useAuth } from './hooks/useAuth'

type LoginValues = { cpf: string; password: string }
type RegisterValues = { name: string; cpf: string; password: string; role: 'USER' | 'ADMIN' }
type AddressValues = { cep: string; number: string; complement?: string; street: string; neighborhood: string; city: string; state: string; main?: boolean }

type AppContextType = {
  auth: AuthInfo | null
  users: User[]
  selectedUserId: string
  addresses: Address[]
  editingAddress: Address | null
  message: string
  error: string
  resetSession: () => void
  handleLogin: (values: LoginValues) => Promise<void>
  handleRegister: (values: RegisterValues) => Promise<void>
  handleUserSelect: (id: string) => Promise<void>
  handleAddressSubmit: (values: AddressValues) => Promise<void>
  handleEditAddress: (address: Address) => void
  handleDeleteAddress: (addressId: string) => Promise<void>
  handleFetchCep: (cepInput: string) => Promise<any | undefined>
}

const AppContext = createContext<AppContextType | undefined>(undefined)

const useAppContext = () => {
  const context = useContext(AppContext)
  if (!context) throw new Error('useAppContext must be used within AppContext.Provider')
  return context
}

function RootLayout() {
  const location = useLocation()
  const navigate = useNavigate()

  useEffect(() => {
    if (location.pathname === '/') {
      navigate({ to: '/login', replace: true })
    }
  }, [location.pathname, navigate])

  return <Outlet />
}

function LoginPage() {
  const { auth, message, error, handleLogin } = useAppContext()

  if (auth) {
    return <Navigate to='/dashboard' replace />
  }

  return (
    <div className='app-shell'>
      <div className='container'>
        <div className='header'>
          <h1>Sistema de Gestão de Endereços</h1>
        </div>
        <Toast message={message} type='success' />
        <Toast message={error} type='error' />
        <LoginForm onSubmit={handleLogin} />
      </div>
    </div>
  )
}

function RegisterPage() {
  const { auth, message, error, handleRegister } = useAppContext()

  if (auth) {
    return <Navigate to='/dashboard' replace />
  }

  return (
    <div className='app-shell'>
      <div className='container'>
        <div className='header'>
          <h1>Sistema de Gestão de Endereços</h1>
        </div>
        <Toast message={message} type='success' />
        <Toast message={error} type='error' />
        <RegisterForm onSubmit={handleRegister} />
      </div>
    </div>
  )
}

function DashboardPage() {
  const {
    auth,
    message,
    error,
    resetSession,
    selectedUserId,
    addresses,
    editingAddress,
    handleUserSelect,
    handleAddressSubmit,
    handleEditAddress,
    handleDeleteAddress,
    handleFetchCep,
    users,
  } = useAppContext()
  const navigate = useNavigate()

  if (!auth) {
    return <Navigate to='/login' replace />
  }

  const adminOptions = auth.role === 'ADMIN' ? users : [{ id: auth.userId, name: auth.name, cpf: auth.cpf, role: auth.role }]

  return (
    <div className='app-shell'>
      <main className='container'>
        <header className='header'>
          <div>
            <h1>Painel de Endereços</h1>
            <p>
              {auth.name} ({auth.role})
            </p>
          </div>
        </header>
        <Toast message={message} type='success' />
        <Toast message={error} type='error' />
        <div className='grid w-full'>
          <AddressForm
            key={editingAddress?.id ?? 'new'}
            initialValues={
              editingAddress
                ? {
                    cep: editingAddress.cep,
                    number: editingAddress.number,
                    complement: editingAddress.complement ?? '',
                    street: editingAddress.street,
                    neighborhood: editingAddress.neighborhood,
                    city: editingAddress.city,
                    state: editingAddress.state,
                    main: editingAddress.main,
                  }
                : undefined
            }
            submitLabel={editingAddress ? 'Atualizar endereço' : 'Adicionar endereço'}
            onSubmit={handleAddressSubmit}
            onFetchCep={handleFetchCep}
          />
          <AddressPanel
            authRole={auth.role}
            currentUserName={auth.name}
            users={adminOptions}
            selectedUserId={selectedUserId}
            onUserSelect={handleUserSelect}
            addresses={addresses}
            onEdit={handleEditAddress}
            onDelete={handleDeleteAddress}
          />
        </div>
        <div className='flex'>
            <Button
              variant='destructive'
              onClick={() => {
                resetSession()
                navigate({ to: '/login' })
              }}
            >
              Sair
            </Button>
          </div>
      </main>
    </div>
  )
}

function NotFoundPage() {
  return <Navigate to='/login' replace />
}

const rootRoute = createRootRoute({ component: RootLayout })
const loginRoute = createRoute({ getParentRoute: () => rootRoute, path: 'login', component: LoginPage })
const registerRoute = createRoute({ getParentRoute: () => rootRoute, path: 'register', component: RegisterPage })
const dashboardRoute = createRoute({ getParentRoute: () => rootRoute, path: 'dashboard', component: DashboardPage })
const notFoundRoute = createRoute({ getParentRoute: () => rootRoute, path: '*', component: NotFoundPage })
rootRoute.addChildren([loginRoute, registerRoute, dashboardRoute, notFoundRoute])

const loginRouteApi = getRouteApi(loginRoute.id)
const registerRouteApi = getRouteApi(registerRoute.id)
const dashboardRouteApi = getRouteApi(dashboardRoute.id)
const notFoundRouteApi = getRouteApi(notFoundRoute.id)
const _routeApis = [loginRouteApi, registerRouteApi, dashboardRouteApi, notFoundRouteApi]
void _routeApis

const router = createRouter({
  routeTree: rootRoute,
  history: createBrowserHistory(),
})

function App() {
  const { auth, setAuth, logout } = useAuth()
  const [users, setUsers] = useState<User[]>([])
  const [selectedUserId, setSelectedUserId] = useState<string>('')
  const [addresses, setAddresses] = useState<Address[]>([])
  const [editingAddress, setEditingAddress] = useState<Address | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const resetSession = () => {
    logout()
    setUsers([])
    setAddresses([])
    setSelectedUserId('')
    setEditingAddress(null)
    setMessage('')
    setError('')
  }

  const loadData = async (desiredUserId?: string) => {
    if (!auth) return

    try {
      if (auth.role === 'ADMIN') {
        const allUsers = await fetchUsers()
        setUsers(allUsers)
        const target = desiredUserId || selectedUserId || allUsers[0]?.id
        if (!target) return
        setSelectedUserId(target)
        const foundAddresses = await fetchAddresses(target)
        setAddresses(foundAddresses)
      } else {
        const foundAddresses = await fetchAddresses(auth.userId)
        setAddresses(foundAddresses)
        setSelectedUserId(auth.userId)
      }
    } catch (err: any) {
      setError(err?.message || 'Erro ao carregar dados')
    }
  }

  useEffect(() => {
    if (auth) loadData()
  }, [auth])

  const handleLogin = async (values: LoginValues) => {
    setError('')
    setMessage('')

    try {
      const user = await login(values)
      const token = `Basic ${btoa(`${values.cpf}:${values.password}`)}`
      setAuth({ userId: user.id, name: user.name, role: user.role, cpf: user.cpf, token })
      setMessage('Usuário autenticado com sucesso')
      router.navigate({ to: '/dashboard' })
    } catch (err: any) {
      setError(err?.message || 'Falha no login')
    }
  }

  const handleRegister = async (values: RegisterValues) => {
    setError('')
    setMessage('')

    try {
      await register(values)
      setMessage('Cadastro realizado. Faça login')
      router.navigate({ to: '/login' })
    } catch (err: any) {
      setError(err?.message || 'Falha no cadastro')
    }
  }

  const handleUserSelect = async (id: string) => {
    setSelectedUserId(id)
    await loadData(id)
  }

  const handleAddressSubmit = async (values: AddressValues) => {
    if (!auth) return

    const targetUserId = auth.role === 'ADMIN' ? selectedUserId : auth.userId
    if (!targetUserId) return

    setError('')
    setMessage('')

    try {
      if (editingAddress) {
        await updateAddress(targetUserId, editingAddress.id, { ...values, main: !!values.main })
        setMessage('Endereço atualizado')
      } else {
        await createAddress(targetUserId, { ...values, main: !!values.main })
        setMessage('Endereço cadastrado')
      }
      setEditingAddress(null)
      await loadData(targetUserId)
    } catch (err: any) {
      setError(err?.message || 'Falha ao salvar endereço')
    }
  }

  const handleEditAddress = (address: Address) => setEditingAddress(address)

  const handleDeleteAddress = async (addressId: string) => {
    if (!auth) return

    const targetUserId = auth.role === 'ADMIN' ? selectedUserId : auth.userId
    if (!targetUserId) return

    try {
      await deleteAddress(targetUserId, addressId)
      setMessage('Endereço excluído')
      await loadData(targetUserId)
    } catch (err: any) {
      setError(err?.message || 'Falha ao excluir endereço')
    }
  }

  const handleFetchCep = async (cepInput: string) => {
    const cepNumbers = cepInput?.replace(/\D/g, '')
    if (!cepNumbers || cepNumbers.length !== 8) return undefined

    try {
      return await fetchCep(`${cepNumbers.substring(0, 5)}-${cepNumbers.substring(5)}`)
    } catch {
      return undefined
    }
  }

  const contextValue = useMemo(
    () => ({
      auth,
      users,
      selectedUserId,
      addresses,
      editingAddress,
      message,
      error,
      resetSession,
      handleLogin,
      handleRegister,
      handleUserSelect,
      handleAddressSubmit,
      handleEditAddress,
      handleDeleteAddress,
      handleFetchCep,
    }),
    [
      auth,
      users,
      selectedUserId,
      addresses,
      editingAddress,
      message,
      error,
      resetSession,
      handleLogin,
      handleRegister,
      handleUserSelect,
      handleAddressSubmit,
      handleEditAddress,
      handleDeleteAddress,
      handleFetchCep,
    ],
  )

  return (
    <AppContext.Provider value={contextValue}>
      <RouterProvider router={router} />
    </AppContext.Provider>
  )
}

export default App;
