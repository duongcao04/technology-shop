import { useEffect, useState } from 'react'

import { message } from 'antd'

import { clientFirebaseService } from '@/lib/firebase/services'

import { Product } from '../../../api/products/type'

type DataType = Product & {
    key: React.Key
}

export const useGetAllProduct = (path: string) => {
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

    return { products: data, isLoading }
}
