'use client'

import React, { useState } from 'react'

import { IconEye } from '@tabler/icons-react'
import { Button, Select, Tag, Tooltip, message } from 'antd'
import { ColumnsType } from 'antd/es/table'

import { firebaseService } from '@/lib/firebase/services'
import { vndFormated } from '@/lib/utils'

import { Order } from '../../api/orders/type'
import CustomizeTable from '../components/CustomizeTable'
import PageHeading from '../components/PageHeading'
import OrderDetailDrawer from './components/OrderDetailDrawer'
import { useGetAllOrder } from './hooks/useOrder'

type DataType = Order & {
    key: React.Key
}

export type FormProps = {
    type: 'CREATE' | 'UPDATE'
    initialValues: object
}

export const orderStatus: { name: string; color: string }[] = [
    {
        name: 'Đang giao',
        color: 'yellow',
    },
    { name: 'Đã giao', color: 'green' },
]

export default function OrdersPage() {
    const { orders, isLoading } = useGetAllOrder('orders')
    const [selectedId, setSelectedId] = useState('')
    const [isDrawerOpen, setDrawerOpen] = useState(false)

    const columns: ColumnsType<DataType> = [
        {
            title: 'Mã đặt hàng',
            dataIndex: 'id',
        },
        {
            title: 'Người đặt hàng',
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
            title: 'Trạng thái',
            dataIndex: 'status',
            render(value) {
                return (
                    <Tag
                        color={
                            orderStatus.find((item) => item.name === value)
                                ?.color
                        }
                    >
                        {value}
                    </Tag>
                )
            },
        },
        {
            title: 'Số điện thoại',
            dataIndex: 'phone',
        },
        {
            title: 'Tổng tiền',
            dataIndex: 'totalPrice',
            render(value) {
                return <p className="font-semibold">{vndFormated(value)}</p>
            },
            sorter: {
                compare: (a, b) => Number(a.totalPrice) - Number(b.totalPrice),
                multiple: 2,
            },
        },
        {
            title: 'Ngày đặt hàng',
            dataIndex: 'orderDate',
            render(value) {
                const date = new Date(value)
                const formater = date.toLocaleString('vi-VN', {
                    timeZone: 'Asia/Ho_Chi_Minh',
                })
                return (
                    <p>
                        {formater.split(' ')[0]} -{' '}
                        <span className="font-semibold">
                            {formater.split(' ')[1]}
                        </span>
                    </p>
                )
            },
            sorter: {
                compare: (a, b) =>
                    new Date(a.orderDate).getTime() -
                    new Date(b.orderDate).getTime(),
                multiple: 2,
            },
        },
        {
            title: 'Hành động',
            dataIndex: 'id',
            render: (id) => {
                return (
                    <div className="flex items-center justify-start gap-3">
                        <Tooltip
                            color="blue"
                            placement="topLeft"
                            title="Chỉnh sửa trạng thái đơn hàng"
                        >
                            <Select
                                size="large"
                                placeholder="Trạng thái đơn hàng"
                                value={
                                    orders.find((item) => item.id === id)
                                        ?.status
                                }
                                disabled={
                                    orders.find((item) => item.id === id)
                                        ?.status === 'Đã giao'
                                }
                                onChange={async (value) => {
                                    await firebaseService.updateById(
                                        'orders',
                                        id,
                                        {
                                            status:
                                                value === 'dangGiao'
                                                    ? 'Đang giao'
                                                    : 'Đã giao',
                                        }
                                    )
                                    message.success(
                                        'Thay đổi trạng thái đơn hàng thành công!'
                                    )
                                }}
                                style={{ width: 200 }}
                                options={[
                                    { value: 'dangGiao', label: 'Đang giao' },
                                    { value: 'daGiao', label: 'Đã giao' },
                                ]}
                                optionRender={(option) => {
                                    const color = orderStatus.find(
                                        (item) =>
                                            item.name === option.data.label
                                    )?.color
                                    return (
                                        <Tag color={color}>
                                            {option.data.label}
                                        </Tag>
                                    )
                                }}
                            />
                        </Tooltip>
                        <Tooltip
                            color="blue"
                            placement="topLeft"
                            title="Chi tiết đơn hàng"
                        >
                            <Button
                                color="blue"
                                variant="filled"
                                size="large"
                                icon={<IconEye />}
                                onClick={() => {
                                    setDrawerOpen(true)
                                    setSelectedId(id)
                                }}
                            />
                        </Tooltip>
                    </div>
                )
            },
        },
    ]

    return (
        <>
            <div>
                <PageHeading
                    title={'Quản lý đơn đặt hàng'}
                    count={orders.length}
                />
            </div>
            <OrderDetailDrawer
                orderId={selectedId}
                isDrawerOpen={isDrawerOpen}
                setDrawerOpen={setDrawerOpen}
            />
            <div className="mt-6">
                <CustomizeTable<DataType>
                    columns={columns}
                    data={orders}
                    isLoading={isLoading}
                />
            </div>
        </>
    )
}
