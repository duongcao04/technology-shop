import { useEffect, useState } from 'react'

import { message } from 'antd'

import { clientFirebaseService, firebaseService } from '@/lib/firebase/services'

import { Order } from '../../../api/orders/type'

type DataType = Order & {
    key: React.Key
}

export const useGetAllOrder = (path: string) => {
    const [data, setData] = useState<DataType[]>([])
    const [isLoading, setLoading] = useState<boolean>(true)

    useEffect(() => {
        try {
            setLoading(true)

            // Đăng ký lắng nghe thay đổi từ Firebase
            const unsubscribe = clientFirebaseService.subscribe(
                path,
                (rawData) => {
                    const formattedData = (rawData as DataType[]).map(
                        (item) => ({
                            ...item,
                            key: item.id,
                        })
                    )
                    setData(formattedData)
                    setLoading(false)
                    message.success('Dữ liệu đã được cập nhật')
                }
            )

            // Hủy lắng nghe khi component unmount
            return () => {
                unsubscribe()
                console.log(`Đã hủy lắng nghe thay đổi từ ${path}`)
            }
        } catch (error) {
            console.error(`Lỗi khi lắng nghe dữ liệu từ ${path}:`, error)
            setLoading(false)
        }
    }, [path])

    return { orders: data, isLoading }
}

export const useGetOrderById = (path: string, id: string) => {
    const [data, setData] = useState<DataType>({} as DataType)
    const [isLoading, setLoading] = useState<boolean>(true)

    useEffect(() => {
        // Sửa: Sử dụng IIFE (Immediately Invoked Function Expression) đúng cách
        ;(async () => {
            try {
                setLoading(true)

                const result = await firebaseService.getById('orders', id)
                setData(result as DataType)
            } catch (error) {
                console.error(`Lỗi khi lấy dữ liệu từ ${path}/${id}:`, error)
            } finally {
                // Đảm bảo luôn set loading thành false khi hoàn thành
                setLoading(false)
            }
        })() // Thêm dấu ngoặc để gọi hàm async ngay lập tức

        // Cleanup function
        return () => {
            // Cleanup nếu cần
        }
    }, [path, id])

    return { order: data, isLoading }
}
