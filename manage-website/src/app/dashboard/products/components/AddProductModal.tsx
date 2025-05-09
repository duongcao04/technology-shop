'use client'

import React, { useState } from 'react'

import { Button, Steps, message, theme } from 'antd'
import { FormikProps, useFormik } from 'formik'
import * as yup from 'yup'

import { firebaseService } from '@/lib/firebase/services'

import { Product } from '../../../api/products/type'
import CustomizeModal from '../../components/CustomizeModal'
import { useAddProductStore } from '../store/productStore'
import AddDescriptionForm from './forms/AddDescriptionForm'
import AddProductForm from './forms/AddProductForm'
import UploadImageForm from './forms/UploadImageForm'

type Props = {
    trigger: React.ReactNode
    isModalOpen: boolean
    setOpenModal: React.Dispatch<React.SetStateAction<boolean>>
}
const validationSchema = yup.object({
    name: yup.string().required('Vui lòng nhập tên sản phẩm!'),
    price: yup.string().required('Vui lòng nhập giá bán!'),
    discountPercent: yup.string().optional(),
    imageUrl: yup.string().required('Vui lòng chọn hình ảnh sản phẩm!'),
    description: yup.string().required('Vui lòng nhập mô tả sản phẩm!'),
})

export default function AddProductModal({
    trigger,
    isModalOpen,
    setOpenModal,
}: Props) {
    const { formType, initialValues } = useAddProductStore()

    const { token } = theme.useToken()
    const [current, setCurrent] = useState(0)

    const next = () => {
        setCurrent(current + 1)
    }

    const prev = () => {
        setCurrent(current - 1)
    }

    const contentStyle: React.CSSProperties = {
        lineHeight: '260px',
        textAlign: 'center',
        color: token.colorTextTertiary,
        backgroundColor: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        border: `1px dashed ${token.colorBorder}`,
        marginTop: 16,
    }

    const formik: FormikProps<Product> = useFormik<Product>({
        initialValues,
        validationSchema,
        enableReinitialize: true,
        onSubmit: async (values) => {
            const newProduct = {
                name: values.name,
                description: values.description,
                discountPercent: values.discountPercent,
                imageUrl: values.imageUrl,
                price: values.price,
            }

            if (formType === 'CREATE') {
                const saved = await firebaseService.push('products', newProduct)
                if (saved) {
                    message.success('Thêm mới sản phẩm thành công!')
                    setOpenModal(false)
                    formik.resetForm()
                    setCurrent(0)
                }
                return
            } else {
                await firebaseService.updateById(
                    'products',
                    initialValues.id,
                    newProduct
                )
                message.success('Cập nhật sản phẩm thành công!')
                setOpenModal(false)
                formik.resetForm()
                setCurrent(0)
                return
            }
        },
    })

    const steps = [
        {
            title: 'Chung',
            content: <AddProductForm formik={formik} />,
        },
        {
            title: 'Ảnh',
            content: <UploadImageForm formik={formik} />,
        },
        {
            title: 'Mô tả',
            content: <AddDescriptionForm formik={formik} />,
        },
    ]

    const items = steps.map((item) => ({ key: item.title, title: item.title }))

    return (
        <CustomizeModal
            isModalOpen={isModalOpen}
            props={{
                title:
                    formType === 'CREATE'
                        ? 'Thêm mới sản phẩm'
                        : 'Cập nhật sản phẩm',
                wrapClassName: 'w-screen',
                centered: true,
                width: {
                    xs: '90%',
                    sm: '80%',
                    md: '70%',
                    lg: '60%',
                    xl: '50%',
                    xxl: '40%',
                },
                footer: <div></div>,
                onClose: () => {
                    setOpenModal(false)
                },
                onCancel: () => {
                    setOpenModal(false)
                },
            }}
            trigger={trigger}
        >
            <Steps current={current} items={items} />
            <form onSubmit={formik.handleSubmit}>
                <div style={current !== 1 ? contentStyle : {}} className="p-5">
                    {steps[current].content}
                </div>
                <div
                    style={{ marginTop: 24 }}
                    className="w-full flex items-center justify-end"
                >
                    {current > 0 && (
                        <Button
                            style={{ margin: '0 8px' }}
                            onClick={() => prev()}
                        >
                            Trở về
                        </Button>
                    )}
                    <Button
                        type="primary"
                        onClick={async () => {
                            if (current === 0) {
                                // Step 1: Validate name, price, and discount
                                const fieldsToValidate = [
                                    'name',
                                    'price',
                                    'discountPercent',
                                ]

                                // Touch fields to trigger validation UI
                                fieldsToValidate.forEach((field) =>
                                    formik.setFieldTouched(field, true)
                                )

                                // Check validity
                                const isValid = fieldsToValidate.every(
                                    (field) =>
                                        validationSchema.fields[
                                            field
                                        ].isValidSync(formik.values[field])
                                )

                                if (isValid) {
                                    next()
                                } else {
                                    message.error(
                                        'Vui lòng kiểm tra lại thông tin!'
                                    )
                                }
                            } else if (current === 1) {
                                // Step 2: Validate image
                                formik.setFieldTouched('imageUrl', true)

                                if (
                                    await validationSchema
                                        .validateAt('imageUrl', formik.values)
                                        .then(() => true)
                                        .catch(() => false)
                                ) {
                                    next()
                                } else {
                                    message.error(
                                        'Vui lòng chọn hình ảnh sản phẩm!'
                                    )
                                }
                            } else if (current === 2) {
                                // Step 3: Final submission
                                formik.handleSubmit()
                            }
                        }}
                    >
                        {current < 2 ? 'Tiếp theo' : 'Hoàn thành'}
                    </Button>
                </div>
            </form>
        </CustomizeModal>
    )
}
