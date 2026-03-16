import { useMemo } from 'react'
import type { Address, User } from '../../types'
import { Button } from '@/components/ui/button'
import { Separator } from '../ui/separator'

type AddressPanelProps = {
  authRole: 'ADMIN' | 'USER'
  currentUserName: string
  users: User[]
  selectedUserId: string
  onUserSelect: (id: string) => void
  addresses: Address[]
  onEdit: (address: Address) => void
  onDelete: (id: string) => void
}

export function AddressPanel({
  authRole,
  users,
  selectedUserId,
  onUserSelect,
  addresses,
  onEdit,
  onDelete,
}: AddressPanelProps) {

  const mainCount = useMemo(() => addresses.filter((a) => a.main).length, [addresses])

  return (
    <section className="rounded-xl border p-4 bg-white/80 shadow-sm mt-5">
      <h2 className="text-lg font-bold mb-3 text-[#f44336]">Endereços</h2>

      {authRole === 'ADMIN' && (
        <div className="mb-3">
          <label htmlFor="userSelect" className="block text-sm font-medium text-slate-600">Usuário</label>
          <select
            id="userSelect"
            value={selectedUserId}
            className="mt-1 block w-full rounded-lg border border-slate-300 px-3 py-2 bg-white"
            onChange={(e) => onUserSelect(e.target.value)}
          >
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.name} ({user.cpf})
              </option>
            ))}
          </select>
        </div>
      )}

      <p className="text-sm mb-2">{addresses.length} endereços. Principal: {mainCount}</p>
      <div className="address-list">
        {addresses.map((address) => (
          <article key={address.id} className={`gap-6 flex flex-col ${address.main ? 'primary' : ''}`}>
            <div className=''>
              <p className="font-semibold text-left">{address.street}, {address.number}{address.complement ? `, ${address.complement}` : ''}</p>
              <p className="text-sm text-slate-600 w-32 text-left">{address.neighborhood} - {address.city}/{address.state} - CEP {address.cep}</p>
              <p className='text-left text-sm w-full font-bold mt-4'>{address.main ? 'Principal' : 'Secundário'}</p>
            </div>
            <Separator className='bg-gray-200 h-0.5'></Separator>
            <div className="flex gap-2">
              <Button onClick={() => onEdit(address)}>
                Editar
              </Button>
              <Button variant="outline" onClick={() => onDelete(address.id)}>
                Excluir
              </Button>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
