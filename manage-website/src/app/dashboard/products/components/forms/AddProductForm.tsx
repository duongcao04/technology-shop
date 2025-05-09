import React from 'react'

import { Form, Input, InputNumber } from 'antd'
import { FormikProps } from 'formik'

import { Product } from '../../../../api/products/type'

export default function AddProductForm({
    formik,
}: {
    formik: FormikProps<Product>
}) {
    return (
        <>
            <Form.Item label="Tên sản phẩm" required>
                <Input
                    id="name"
                    name="name"
                    value={formik.values.name}
                    onChange={formik.handleChange}
                    size="large"
                    placeholder="e.g. iPhone 16"
                    status={Boolean(formik.errors.name) ? 'error' : ''}
                />
                {Boolean(formik.errors.name) && (
                    <p className="text-left text-red-500">
                        {formik.errors.name}
                    </p>
                )}
            </Form.Item>

            <Form.Item label="Giá bán" required>
                <InputNumber
                    id="price"
                    name="price"
                    addonAfter="VNĐ"
                    style={{ width: '100%' }}
                    value={formik.values.price}
                    onChange={(value) => {
                        formik.setFieldValue('price', value?.toString())
                    }}
                    size="large"
                    placeholder="e.g. 200 000 000"
                    type="number"
                    status={Boolean(formik.errors.price) ? 'error' : ''}
                />
                {Boolean(formik.errors.price) && (
                    <p className="mt-1 text-sm text-left text-red-500">
                        {formik.errors.price}
                    </p>
                )}
            </Form.Item>

            <Form.Item label="Giảm giá">
                <InputNumber
                    id="discountPercent"
                    name="discountPercent"
                    value={formik.values.discountPercent}
                    onChange={(value) => {
                        formik.setFieldValue(
                            'discountPercent',
                            value?.toString()
                        )
                    }}
                    addonAfter="%"
                    style={{ width: '100%' }}
                    size="large"
                    placeholder="e.g. 10"
                    status={
                        Boolean(formik.errors.discountPercent) ? 'error' : ''
                    }
                />
                {Boolean(formik.errors.discountPercent) && (
                    <p className="text-left text-red-500">
                        {formik.errors.discountPercent}
                    </p>
                )}
            </Form.Item>
        </>
    )
}
