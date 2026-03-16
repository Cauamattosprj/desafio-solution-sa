import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Field, FieldDescription, FieldLabel, FieldSeparator } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Link } from '@tanstack/react-router'

const logoSrc = new URL('../../assets/solution-logo-2.svg', import.meta.url).href

const registerSchema = z.object({
  name: z.string().min(3, 'Nome obrigatório'),
  cpf: z
    .string()
    .min(11, 'CPF precisa ser informado')
    .regex(/^\d{11}$|^\d{3}\.\d{3}\.\d{3}-\d{2}$/, 'CPF deve estar no formato 000.000.000-00 ou 00000000000'),
  password: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
  role: z.enum(['USER', 'ADMIN']),
})

type RegisterFormValues = z.infer<typeof registerSchema>

export function RegisterForm({ onSubmit }: { onSubmit: (values: RegisterFormValues) => Promise<void> | void }) {
  const form = useForm<RegisterFormValues>({ resolver: zodResolver(registerSchema), defaultValues: { name: '', cpf: '', password: '', role: 'USER' } })

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="rounded-xl border p-6 bg-white/75 shadow-lg max-w-md w-full space-y-6">
      <div className="text-center">
        <img src={logoSrc} alt="Logo" className='w-48 mb-4 mx-auto'/>
        <h1 className="text-2xl text-[#f44336]">Cadastro de Usuário</h1>
        <p className="text-sm text-slate-500 mt-1">Crie uma conta para acessar o painel</p>
      </div>

      <Field>
        <FieldLabel htmlFor="name">Nome</FieldLabel>
        <Input id="name" placeholder="Nome completo" {...form.register('name')} />
        <FieldDescription className="text-red-500">{form.formState.errors.name?.message as string}</FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="cpf">CPF</FieldLabel>
        <Input id="cpf" placeholder="000.000.000-00" {...form.register('cpf')} />
        <FieldDescription className="text-red-500">{form.formState.errors.cpf?.message as string}</FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="password">Senha</FieldLabel>
        <Input id="password" type="password" placeholder="Senha" {...form.register('password')} />
        <FieldDescription className="text-red-500">{form.formState.errors.password?.message as string}</FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="role">Tipo</FieldLabel>
        <select className="w-full bg-white rounded-lg border border-slate-300 px-3 py-2" id="role" {...form.register('role')}>
          <option value="USER">Usuário</option>
          <option value="ADMIN">Administrador</option>
        </select>
      </Field>

      <Button type="submit" className="w-full">Registrar</Button>
      <FieldSeparator>ou</FieldSeparator>
      <FieldDescription className='w-full text-center'>
        <Link to='/login'>Ir para login</Link>
      </FieldDescription>
    </form>
  )
}
