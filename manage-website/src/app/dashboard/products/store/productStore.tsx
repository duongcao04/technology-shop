import { create } from 'zustand'

import { Product } from '../../../api/products/type'

// Define the state and actions
interface AddProductState {
    formType: 'CREATE' | 'UPDATE'
    initialValues: Product
    setUpdateForm: (initialValues: Product) => void
    setCreateForm: () => void
    setValue: (key: keyof typeof initialValues, value: string) => void
}

export type FormProps = {
    formType: 'CREATE' | 'UPDATE'
    initialValues: Product
}

const initialValues = {
    id: '',
    description: '',
    discountPercent: '',
    imageUrl: '',
    name: '',
    price: '',
}

// Create the Zustand store with types
export const useAddProductStore = create<AddProductState>((set) => ({
    formType: 'CREATE',
    initialValues,
    setUpdateForm: (initialValues: Product) =>
        set(() => ({ formType: 'UPDATE', initialValues })),
    setCreateForm: () => set(() => ({ formType: 'CREATE', initialValues })),
    setValue: (key: keyof typeof initialValues, value: string) =>
        set((state) => {
            const formType = state.formType
            const values = {
                ...state.initialValues,
                [key]: value,
            }
            return {
                formType,
                initialValues: values,
            }
        }),
}))
