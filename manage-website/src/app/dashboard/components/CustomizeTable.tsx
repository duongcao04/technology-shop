'use client'

import React from 'react'

import { Table } from 'antd'
import type { TableColumnsType, TableProps } from 'antd'

interface Props<DataType> {
    data: DataType[]
    columns: TableColumnsType<DataType>
    isLoading: boolean
    // onChange: TableProps<DataType>['onChange']
}

export default function CustomizeTable<DataType extends { key: React.Key }>({
    data,
    columns,
    isLoading,
}: Props<DataType>) {
    const onChange: TableProps<DataType>['onChange'] = (
        pagination,
        filters,
        sorter,
        extra
    ) => {
        console.log('params', pagination, filters, sorter, extra)
    }

    return (
        <Table<DataType>
            columns={columns}
            dataSource={data}
            onChange={onChange}
            pagination={{
                defaultPageSize: 6,
                position: ['bottomCenter'],
            }}
            loading={isLoading}
        />
    )
}
