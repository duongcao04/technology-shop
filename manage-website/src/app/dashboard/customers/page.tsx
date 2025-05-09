'use client'

import React, { useEffect, useState } from 'react'

import { IconDisabled } from '@tabler/icons-react'
import { Button, Image, Tooltip } from 'antd'
import { ColumnsType } from 'antd/es/table'

import { User } from '../../api/users/type'
import CustomizeTable from '../components/CustomizeTable'
import PageHeading from '../components/PageHeading'

type DataType = User & {
    key: React.Key
}
const columns: ColumnsType<DataType> = [
    {
        title: 'Mã số',
        dataIndex: 'uid',
    },
    {
        title: 'Ảnh đại diện',
        dataIndex: 'photoUrl',
        render(value) {
            return (
                <div className="size-20">
                    <Image
                        src={value}
                        alt="Ảnh đại diện"
                        className="w-full h-full"
                    />
                </div>
            )
        },
    },
    {
        title: 'Tên',
        dataIndex: 'displayName',
        render(value) {
            return <p className="font-semibold">{value}</p>
        },
        sorter: {
            compare: (a, b) => a.displayName.localeCompare(b.displayName),
            multiple: 3,
        },
    },
    {
        title: 'Email',
        dataIndex: 'email',
    },
    {
        title: 'Số điện thoại',
        dataIndex: 'phoneNumber',
    },
    {
        title: 'Hành động',
        render: () => {
            return (
                <div className="flex items-center justify-start gap-3">
                    <Tooltip
                        color="red"
                        placement="topLeft"
                        title="Chặn người dùng"
                    >
                        <Button
                            danger
                            variant="filled"
                            size="large"
                            icon={<IconDisabled />}
                        >
                            Chặn
                        </Button>
                    </Tooltip>
                </div>
            )
        },
    },
]

export default function CustomersPage() {
    const [customers, setsCustomers] = useState<DataType[]>([])
    const [isLoading, setLoading] = useState(true)

    useEffect(() => {
        try {
            setLoading(true)
            fetch('/api/users')
                .then((response) => response.json())
                .then((data) => {
                    return setsCustomers(
                        data.map((item: DataType) => ({
                            ...item,
                            key: item.uid,
                        }))
                    )
                })
                .catch((error) =>
                    console.error('Error fetching products:', error)
                )
        } catch (error) {
            console.log(error)
        } finally {
            setLoading(false)
        }
    }, [])
    console.log(customers)

    return (
        <>
            <div>
                <PageHeading
                    title={'Quản lý người dùng'}
                    count={customers.length}
                />
            </div>
            <div className="mt-6">
                <CustomizeTable<DataType>
                    columns={columns}
                    data={customers}
                    isLoading={isLoading}
                />
            </div>
        </>
    )
}
