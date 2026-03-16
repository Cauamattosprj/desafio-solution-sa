import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Field, FieldDescription, FieldLabel, FieldSeparator } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Link } from '@tanstack/react-router'

const logoSrc = new URL('../assets/solution-logo-2.svg', import.meta.url).href






const loginSchema = z.object({
  cpf: z
    .string()
    .min(11, 'CPF precisa ser informado')
    .regex(/^\d{11}$|^\d{3}\.\d{3}\.\d{3}-\d{2}$/, 'CPF deve estar no formato 000.000.000-00 ou 00000000000'),
  password: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
})

type LoginFormValues = z.infer<typeof loginSchema>

export function LoginForm({ onSubmit }: { onSubmit: (values: LoginFormValues) => Promise<void> | void }) {
  const form = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema), defaultValues: { cpf: '', password: '' } })

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className={cn('rounded-xl border p-6 bg-white/75 shadow-lg max-w-md w-full space-y-6')}>
      <div className="text-center">
        <img src={logoSrc} alt="Logo" className='w-48 mb-4 mx-auto'/>
        <h1 className="text-2xl text-[#f44336]">Login de Usuário</h1>
        <p className="text-sm text-slate-500 mt-1">Entre com seu CPF e senha</p>
      </div>

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

      <Button type="submit" className="w-full">Entrar</Button>

      <FieldSeparator>ou</FieldSeparator>

      <Field>
        <FieldDescription className="text-center">
          <Link to="/register">
          Não tem conta? Faça o cadastro.
          </Link>

        </FieldDescription>
      </Field>
    </form>
  )
}
