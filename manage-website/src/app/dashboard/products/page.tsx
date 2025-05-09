'use client'

import React, { useState } from 'react'

import { IconEdit, IconPlus, IconTrash } from '@tabler/icons-react'
import {
    Button,
    Image,
    Popconfirm,
    PopconfirmProps,
    Tag,
    Tooltip,
    message,
} from 'antd'
import { ColumnsType } from 'antd/es/table'

import { vndFormated } from '@/lib/utils'

import { Product } from '../../api/products/type'
import CustomizeTable from '../components/CustomizeTable'
import PageHeading from '../components/PageHeading'
import AddProductModal from './components/AddProductModal'
import { useGetAllProduct } from './hooks/useProduct'
import { useAddProductStore } from './store/productStore'

const handleRemove = async (productId: string) => {
    try {
        const response = await fetch('/api/products', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ productId: productId }), // Đổi 'productId' thành 'id' để khớp với API
        })

        const result = await response.json()

        if (response.ok) {
            message.success('Sản phẩm đã được xóa thành công')
        } else {
            message.error(`Không thể xóa sản phẩm: ${result.error}`)
        }
    } catch (error) {
        message.error('Đã xảy ra lỗi khi xóa sản phẩm')
        console.error('Error while removing product:', error)
    }
}

type DataType = Product & {
    key: React.Key
}

export type FormProps = {
    type: 'CREATE' | 'UPDATE'
    initialValues: object
}

export default function ProductsPage() {
    const [isModalOpen, setOpenModal] = useState(false)
    const { products, isLoading } = useGetAllProduct('products')
    const { setCreateForm, setUpdateForm } = useAddProductStore()

    const columns: ColumnsType<DataType> = [
        {
            title: 'Mã sản phẩm',
            dataIndex: 'id',
        },
        {
            title: 'Ảnh thu nhỏ',
            dataIndex: 'imageUrl',
            render: (cellValue) => {
                return (
                    <div className="size-20">
                        <Image
                            src={cellValue}
                            alt="Thumbnail"
                            className="w-full h-full"
                        />
                    </div>
                )
            },
        },
        {
            title: 'Tên sản phẩm',
            dataIndex: 'name',
            render(value) {
                return <p className="font-semibold">{value}</p>
            },
            sorter: {
                compare: (a, b) => a.name.localeCompare(b.name),
                multiple: 3,
            },
        },
        {
            title: 'Giá gốc',
            dataIndex: 'price',
            render(value) {
                return <p className="font-semibold">{vndFormated(value)}</p>
            },
            sorter: {
                compare: (a, b) => Number(a.price) - Number(b.price),
                multiple: 2,
            },
        },
        {
            title: 'Giảm giá (%)',
            dataIndex: 'discountPercent',
            render: (cellValue) => {
                if (Number(cellValue) === 0) {
                    return <div></div>
                }

                return <Tag color="error">-{cellValue}%</Tag>
            },
        },
        {
            title: 'Hành động',
            dataIndex: 'id',
            render: (value) => {
                const confirm: PopconfirmProps['onConfirm'] = () => {
                    handleRemove(value)
                }

                const cancel: PopconfirmProps['onCancel'] = (e) => {
                    console.log(e)
                    message.error('Click on No')
                }

                return (
                    <div className="flex items-center justify-start gap-3">
                        <Tooltip
                            color="yellow"
                            placement="topLeft"
                            title="Chỉnh sửa"
                        >
                            <Button
                                color="yellow"
                                variant="filled"
                                size="large"
                                icon={<IconEdit />}
                                onClick={() => {
                                    const foundProduct = products.findIndex(
                                        (item) => item.id === value
                                    )
                                    setUpdateForm(products[foundProduct])
                                    setOpenModal(true)
                                }}
                            />
                        </Tooltip>
                        <Tooltip color="red" placement="topLeft" title="Xóa">
                            <Popconfirm
                                title="Xóa sản phẩm"
                                description="Bạn có chắc chắn muốn xóa sản phẩm?"
                                onConfirm={confirm}
                                onCancel={cancel}
                                okText="Xóa"
                                cancelText="Hủy"
                                okButtonProps={{
                                    danger: true,
                                }}
                            >
                                <Button
                                    color="red"
                                    variant="filled"
                                    size="large"
                                    icon={<IconTrash />}
                                />
                            </Popconfirm>
                        </Tooltip>
                    </div>
                )
            },
        },
    ]

    return (
        <div className='no-scrollbar'>
            <div>
                <PageHeading
                    title={'Quản lý sản phẩm'}
                    count={products.length}
                />
            </div>
            <AddProductModal
                trigger={
                    <Button
                        icon={<IconPlus />}
                        className="mt-5"
                        size="large"
                        variant="solid"
                        color="blue"
                        onClick={() => {
                            setCreateForm()
                            setOpenModal(true)
                        }}
                    >
                        Thêm mới sản phẩm
                    </Button>
                }
                isModalOpen={isModalOpen}
                setOpenModal={setOpenModal}
            />
            <div className="mt-6">
                <CustomizeTable<DataType>
                    columns={columns}
                    data={products}
                    isLoading={isLoading}
                />
            </div>
        </div>
    )
}
