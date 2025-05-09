import React from 'react'

import { Col, Divider, Drawer, Image, Row, Tag } from 'antd'

import { vndFormated } from '../../../../lib/utils'
import { useGetOrderById } from '../hooks/useOrder'
import { orderStatus } from '../page'

type Props = {
    isDrawerOpen: boolean
    orderId: string
    setDrawerOpen: React.Dispatch<React.SetStateAction<boolean>>
}

export default function OrderDetailDrawer({
    orderId,
    isDrawerOpen,
    setDrawerOpen,
}: Props) {
    const { order, isLoading } = useGetOrderById('orders', orderId)
    const date = new Date(order.orderDate)
    const orderDate = date.toLocaleString('vi-VN', {
        timeZone: 'Asia/Ho_Chi_Minh',
    })

    console.log(order)

    return (
        <Drawer
            closable
            title={
                <div>
                    <Tag
                        color={
                            orderStatus.find(
                                (item) => item.name === order.status
                            )?.color
                        }
                    >
                        {order.status}
                    </Tag>
                    <p>Thông tin đơn đặt hàng</p>
                </div>
            }
            placement="right"
            open={isDrawerOpen}
            loading={isLoading}
            onClose={() => {
                setDrawerOpen(false)
            }}
            width={640}
        >
            <p className="text-lg">Người đặt hàng</p>
            <Row className="mt-4">
                <Col span={12}>
                    <p>Họ và tên:</p>
                    <p>Số điện thoại:</p>
                </Col>
                <Col span={12}>
                    <p className="font-semibold">{order.name}</p>
                    <p className="font-semibold">{order.phone}</p>
                </Col>
            </Row>
            <Divider />
            <p className="text-lg">Thông tin chung</p>
            <Row className="mt-4">
                <Col span={12}>
                    <p>Mã đơn hàng:</p>
                    <p>Địa chỉ nhận hàng:</p>
                    <p>Phương thức thanh toán:</p>
                    <p>Thời gian đặt hàng:</p>
                </Col>
                <Col span={12}>
                    <p className="font-semibold">{order.id}</p>
                    <p className="font-semibold">{order.address}</p>
                    <p className="font-semibold">{order.paymentMethod}</p>
                    <p className="font-semibold">
                        <span>{orderDate.split(' ')[0]} - </span>
                        <span className="font-semibold">
                            {orderDate.split(' ')[1]}
                        </span>
                    </p>
                </Col>
            </Row>
            <Divider />
            <p className="text-lg">Sản phẩm</p>
            <div className="mt-4 space-y-3">
                {order.items &&
                    Object.entries(order.items).map((item) => {
                        const product = item[1]
                        console.log(product)

                        return (
                            <div
                                key={product.id}
                                className="flex items-center justify-between"
                            >
                                <div className="flex items-center justify-start gap-3">
                                    <div className="size-24 border rounded-lg flex items-center justify-center p-1 bg-gray-100">
                                        <Image
                                            src={product.productImage}
                                            alt={product.productName}
                                            className="w-full h-full"
                                        />
                                    </div>
                                    <div className="h-full flex flex-col items-start justify-start">
                                        <p className="text-base font-medium">
                                            {product.productName}
                                        </p>
                                        <p>SL: x{product.quantity}</p>
                                    </div>
                                </div>
                                <div className="flex flex-col items-end">
                                    <p className="text-base font-bold">
                                        {vndFormated(product.price)}
                                    </p>
                                    <p className="text-lg font-bold text-red-500">
                                        {vndFormated(product.subtotal)}
                                    </p>
                                </div>
                            </div>
                        )
                    })}
            </div>
            <hr className="mt-10 mb-5" />
            <div className="flex justify-end items-end">
                <div className="text-right text-base space-y-2">
                    <div className="grid grid-cols-3 gap-8">
                        <p className="col-span-2">Tổng (1 sản phẩm):</p>
                        <p className="font-semibold">
                            {vndFormated(order.totalPrice)}
                        </p>
                    </div>
                    <div className="grid grid-cols-3 gap-8">
                        <p className="col-span-2">Phí vận chuyển:</p>
                        <p className="font-semibold">Miễn phí</p>
                    </div>
                    <div className="grid grid-cols-3 gap-8">
                        <p className="font-semibold col-span-2">Tổng cộng:</p>
                        <p className="font-bold text-xl text-red-500">
                            {vndFormated(order.totalPrice)}
                        </p>
                    </div>
                </div>
            </div>
        </Drawer>
    )
}
