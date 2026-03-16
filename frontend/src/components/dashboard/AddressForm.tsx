import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Field, FieldDescription, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'

const logoSrc = new URL('../../assets/solution-logo-2.svg', import.meta.url).href

const addressSchema = z.object({
  cep: z
    .string()
    .regex(/^\d{5}-\d{3}$|^\d{8}$/, 'CEP deve estar em formato 00000-000 ou 00000000')
    .transform((cep) => cep.replace(/[^\d]/g, '').replace(/(\d{5})(\d{3})/, '$1-$2')),
  number: z.string().min(1, 'Número obrigatório'),
  complement: z.string().optional(),
  street: z.string().min(1, 'Rua obrigatória'),
  neighborhood: z.string().min(1, 'Bairro obrigatório'),
  city: z.string().min(1, 'Cidade obrigatória'),
  state: z.string().length(2, 'Estado deve ter 2 letras'),
  main: z.boolean().optional(),
})

type AddressFormValues = z.infer<typeof addressSchema>

export function AddressForm({
  initialValues,
  onSubmit,
  onFetchCep,
  submitLabel,
}: {
  initialValues?: Partial<AddressFormValues>
  onSubmit: (values: AddressFormValues) => Promise<void> | void
  onFetchCep: (cep: string) => Promise<{ logradouro?: string; bairro?: string; localidade?: string; uf?: string } | undefined>
  submitLabel: string
}) {
  const form = useForm<AddressFormValues>({
    resolver: zodResolver(addressSchema),
    defaultValues: {
      cep: '',
      number: '',
      complement: '',
      street: '',
      neighborhood: '',
      city: '',
      state: '',
      main: false,
      ...initialValues,
    },
  })

  const handleCepBlur = async (cepValue: string) => {
    const data = await onFetchCep(cepValue)
    if (data) {
      if (data.logradouro) form.setValue('street', data.logradouro)
      if (data.bairro) form.setValue('neighborhood', data.bairro)
      if (data.localidade) form.setValue('city', data.localidade)
      if (data.uf) form.setValue('state', data.uf)
    }
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="rounded-xl border p-6 bg-white/75 shadow-lg max-w-2xl w-full space-y-4">
      <img src={logoSrc} alt="Logo" className='w-48 mb-4 mx-auto'/>
      <h2 className="text-lg font-bold text-[#f44336]">Cadastro de Endereço</h2>

      <Field>
        <FieldLabel htmlFor="cep">CEP</FieldLabel>
        <Input id="cep" placeholder="00000-000" {...form.register('cep')} onBlur={(e) => handleCepBlur(e.target.value)} />
        <FieldDescription className="text-red-500">{form.formState.errors.cep?.message as string}</FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="number">Número</FieldLabel>
        <Input id="number" placeholder="Número" {...form.register('number')} />
        <FieldDescription className="text-red-500">{form.formState.errors.number?.message as string}</FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="complement">Complemento</FieldLabel>
        <Input id="complement" placeholder="Complemento" {...form.register('complement')} />
      </Field>

      <Field>
        <FieldLabel htmlFor="street">Rua</FieldLabel>
        <Input id="street" placeholder="Rua" {...form.register('street')} />
      </Field>

      <Field>
        <FieldLabel htmlFor="neighborhood">Bairro</FieldLabel>
        <Input id="neighborhood" placeholder="Bairro" {...form.register('neighborhood')} />
      </Field>

      <Field>
        <FieldLabel htmlFor="city">Cidade</FieldLabel>
        <Input id="city" placeholder="Cidade" {...form.register('city')} />
      </Field>

      <Field>
        <FieldLabel htmlFor="state">Estado</FieldLabel>
        <Input id="state" placeholder="UF" maxLength={2} {...form.register('state')} />
      </Field>

      <div className="flex items-center gap-2">
        <input type="checkbox" className="h-4 w-4" {...form.register('main')} />
        <span>Principal</span>
      </div>

      <Button type="submit" className="w-full">
        {submitLabel}
      </Button>
    </form>
  )
}
