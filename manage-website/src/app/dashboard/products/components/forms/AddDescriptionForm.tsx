'use client'

import React from 'react'

import TextArea from 'antd/es/input/TextArea'
import { FormikProps } from 'formik'

import { Product } from '../../../../api/products/type'

export default function AddDescriptionForm({
    formik,
}: {
    formik: FormikProps<Product>
}) {
    return (
        <TextArea
            placeholder="Thêm mô tả ..."
            value={formik.values.description}
            onChange={formik.handleChange}
            name="description"
            rows={12}
            style={{ color: 'black' }}
            size="large"
        />
    )
}
