import {
    IconArrowLeft,
    IconBrandCodesandbox,
    IconBrandTabler,
    IconTruckDelivery,
    IconUsers,
} from '@tabler/icons-react'

const BASE_DASHBOARD_URI = '/dashboard'
export const sidebarLinks = [
    {
        label: 'Bảng điều khiển',
        href: '/',
        icon: (
            <IconBrandTabler className="h-5 w-5 shrink-0 text-neutral-700 dark:text-neutral-200" />
        ),
    },
    {
        label: 'Sản phẩm',
        href: '/products',
        icon: (
            <IconBrandCodesandbox className="h-5 w-5 shrink-0 text-neutral-700 dark:text-neutral-200" />
        ),
    },
    {
        label: 'Đơn đặt hàng',
        href: '/orders',
        icon: (
            <IconTruckDelivery className="h-5 w-5 shrink-0 text-neutral-700 dark:text-neutral-200" />
        ),
    },
    {
        label: 'Khách hàng',
        href: '/customers',
        icon: (
            <IconUsers className="h-5 w-5 shrink-0 text-neutral-700 dark:text-neutral-200" />
        ),
    },
    {
        label: 'Đăng xuất',
        href: '#',
        icon: (
            <IconArrowLeft className="h-5 w-5 shrink-0 text-neutral-700 dark:text-neutral-200" />
        ),
    },
]

export const SIDEBAR_LINKS = sidebarLinks.map((item) => ({
    ...item,
    href: BASE_DASHBOARD_URI + item.href,
}))
