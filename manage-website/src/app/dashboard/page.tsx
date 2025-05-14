'use client'

import React, { useEffect, useState } from 'react'

import {
    ArrowUpOutlined,
    CheckCircleOutlined,
    DollarOutlined,
    ShoppingCartOutlined,
    SyncOutlined,
    TagOutlined,
    UserOutlined,
} from '@ant-design/icons'
import {
    Avatar,
    Card,
    Col,
    DatePicker,
    Divider,
    Layout,
    List,
    Row,
    Select,
    Space,
    Spin,
    Statistic,
    Table,
    Tag,
    Typography,
} from 'antd'
import {
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    Legend,
    Line,
    LineChart,
    Pie,
    PieChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from 'recharts'

import { firebaseService } from '@/lib/firebase/services'

const { Header, Content } = Layout
const { Title, Text } = Typography
const { Option } = Select
const { RangePicker } = DatePicker

// Định nghĩa các interface
interface OrderItem {
    id: string
    orderId: string
    productId: string
    productName: string
    productImage: string
    price: number
    quantity: number
    subtotal: number
}

interface Order {
    id: string
    userId: string
    name: string
    address: string
    phone: string
    status: string
    orderDate: number
    totalPrice: number
    paymentMethod: string
    items: Record<string, OrderItem>
    updatedAt?: string
}

interface Product {
    name: string
    price: number
    description: string
    discountPercent: number
    imageUrl: string
}

interface User {
    uid: string
    displayName: string
    email: string
    photoUrl: string
    creationTimestamp: number
}

interface DashboardData {
    orders: Record<string, Order>
    products: Record<string, Product>
    users: Record<string, User>
}

const Dashboard: React.FC = () => {
    const [data, setData] = useState<DashboardData | null>(null)
    const [loading, setLoading] = useState<boolean>(true)
    const [timeRange, setTimeRange] = useState<[Date, Date] | null>(null)
    const [statusFilter, setStatusFilter] = useState<string | null>(null)

    // Lấy dữ liệu từ Firebase
    useEffect(() => {
        const fetchData = async () => {
            try {
                const orders = await firebaseService.getAll('orders')
                const products = await firebaseService.getAll('products')
                const users = await firebaseService.getAll('users')

                setData({
                    orders: orders.reduce(
                        (acc, order) => ({ ...acc, [order.id]: order }),
                        {}
                    ),
                    products: products.reduce(
                        (acc, product) => ({ ...acc, [product.id]: product }),
                        {}
                    ),
                    users: users.reduce(
                        (acc, user) => ({ ...acc, [user.uid]: user }),
                        {}
                    ),
                })
            } catch (error) {
                console.error('Lỗi khi lấy dữ liệu:', error)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [])

    // Xử lý dữ liệu để hiển thị thống kê
    const getOrdersArray = () => {
        if (!data) return []
        return Object.values(data.orders)
    }

    const getFilteredOrders = () => {
        let orders = getOrdersArray()

        if (timeRange) {
            const [start, end] = timeRange
            orders = orders.filter((order) => {
                const orderDate = new Date(order.orderDate)
                return orderDate >= start && orderDate <= end
            })
        }

        if (statusFilter) {
            orders = orders.filter((order) => order.status === statusFilter)
        }

        return orders
    }

    // Tính tổng doanh thu
    const calculateTotalRevenue = () => {
        const orders = getFilteredOrders()
        return orders.reduce((total, order) => total + order.totalPrice, 0)
    }

    // Tính số lượng đơn hàng
    const calculateOrderCount = () => {
        return getFilteredOrders().length
    }

    // Tính giá trị đơn hàng trung bình
    const calculateAverageOrderValue = () => {
        const orders = getFilteredOrders()
        const totalRevenue = calculateTotalRevenue()
        return orders.length > 0 ? totalRevenue / orders.length : 0
    }

    // Đếm số lượng sản phẩm đã bán
    const calculateTotalProductsSold = () => {
        const orders = getFilteredOrders()
        return orders.reduce((total, order) => {
            return (
                total +
                Object.values(order.items).reduce(
                    (sum, item) => sum + item.quantity,
                    0
                )
            )
        }, 0)
    }

    // Thống kê đơn hàng theo trạng thái
    const getOrdersByStatus = () => {
        const orders = getOrdersArray()
        const statusCounts: Record<string, number> = {}

        orders.forEach((order) => {
            statusCounts[order.status] = (statusCounts[order.status] || 0) + 1
        })

        return Object.entries(statusCounts).map(([status, count]) => ({
            status,
            count,
        }))
    }

    // Thống kê sản phẩm bán chạy nhất
    const getTopSellingProducts = () => {
        const orders = getOrdersArray()
        const productSales: Record<
            string,
            { count: number; revenue: number; name: string }
        > = {}

        orders.forEach((order) => {
            Object.values(order.items).forEach((item) => {
                if (!productSales[item.productId]) {
                    productSales[item.productId] = {
                        count: 0,
                        revenue: 0,
                        name: item.productName,
                    }
                }
                productSales[item.productId].count += item.quantity
                productSales[item.productId].revenue += item.subtotal
            })
        })

        return Object.entries(productSales)
            .map(([productId, data]) => ({
                productId,
                ...data,
            }))
            .sort((a, b) => b.count - a.count)
            .slice(0, 5)
    }

    // Thống kê doanh thu theo ngày
    const getRevenueByDay = () => {
        const orders = getOrdersArray()
        const revenueByDay: Record<string, number> = {}

        orders.forEach((order) => {
            const date = new Date(order.orderDate).toISOString().split('T')[0]
            revenueByDay[date] = (revenueByDay[date] || 0) + order.totalPrice
        })

        return Object.entries(revenueByDay)
            .map(([date, revenue]) => ({
                date,
                revenue,
            }))
            .sort(
                (a, b) =>
                    new Date(a.date).getTime() - new Date(b.date).getTime()
            )
            .slice(-7) // Lấy 7 ngày gần nhất
    }

    // Định dạng số tiền
    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
        }).format(amount)
    }

    // Danh sách đơn hàng gần đây
    const getRecentOrders = () => {
        return getOrdersArray()
            .sort((a, b) => b.orderDate - a.orderDate)
            .slice(0, 5)
    }

    // Màu sắc cho biểu đồ
    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8']

    // Định dạng trạng thái đơn hàng
    const renderOrderStatus = (status: string) => {
        switch (status) {
            case 'Đã giao':
                return (
                    <Tag color="green">
                        <CheckCircleOutlined /> Đã giao
                    </Tag>
                )
            case 'Đang giao':
                return (
                    <Tag color="blue">
                        <SyncOutlined spin /> Đang giao
                    </Tag>
                )
            default:
                return <Tag color="default">{status}</Tag>
        }
    }

    // Cột cho bảng đơn hàng
    const orderColumns = [
        {
            title: 'Mã đơn hàng',
            dataIndex: 'id',
            key: 'id',
            render: (id: string) => (
                <Text copyable ellipsis>
                    {id}
                </Text>
            ),
        },
        {
            title: 'Khách hàng',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Ngày đặt',
            dataIndex: 'orderDate',
            key: 'orderDate',
            render: (date: number) =>
                new Date(date).toLocaleDateString('vi-VN'),
        },
        {
            title: 'Tổng tiền',
            dataIndex: 'totalPrice',
            key: 'totalPrice',
            render: (price: number) => formatCurrency(price),
        },
        {
            title: 'Trạng thái',
            dataIndex: 'status',
            key: 'status',
            render: renderOrderStatus,
        },
    ]

    if (loading) {
        return (
            <div className="w-full h-full flex items-center justify-center">
                <Spin size="large" />
            </div>
        )
    }

    return (
        <div>
            <Header
                style={{
                    background: 'var(--background)',
                    color: 'var(--foreground)',
                    padding: '0 16px',
                }}
            >
                <div
                    style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                    }}
                >
                    <Title
                        level={3}
                        style={{ margin: '16px 0', color: 'var(--foreground)' }}
                    >
                        Thống kê
                    </Title>
                    <Space>
                        <RangePicker
                            onChange={(dates) =>
                                setTimeRange(dates as [Date, Date])
                            }
                            format="DD/MM/YYYY"
                        />
                        <Select
                            placeholder="Lọc theo trạng thái"
                            style={{ width: 180 }}
                            allowClear
                            onChange={setStatusFilter}
                        >
                            <Option value="Đã giao">Đã giao</Option>
                            <Option value="Đang giao">Đang giao</Option>
                        </Select>
                    </Space>
                </div>
            </Header>
            <Content
                style={{
                    background: 'var(--background)',
                }}
                className="!h-[calc(100vh-118px)] !overflow-y-auto overflow-x-hidden no-scrollbar rounded-lg"
            >
                {/* Thống kê tổng quan */}
                <Row gutter={[16, 16]}>
                    <Col xs={24} sm={12} md={6}>
                        <Card>
                            <Statistic
                                title="Tổng doanh thu"
                                value={calculateTotalRevenue()}
                                precision={0}
                                valueStyle={{ color: '#3f8600' }}
                                prefix={<DollarOutlined />}
                                suffix="đ"
                                formatter={(value) =>
                                    `${new Intl.NumberFormat('vi-VN',{ maximumFractionDigits: 0 }).format(Number(value))}`
                                }
                            />
                        </Card>
                    </Col>
                    <Col xs={24} sm={12} md={6}>
                        <Card>
                            <Statistic
                                title="Số đơn hàng"
                                value={calculateOrderCount()}
                                valueStyle={{ color: '#1890ff' }}
                                prefix={<ShoppingCartOutlined />}
                            />
                        </Card>
                    </Col>
                    <Col xs={24} sm={12} md={6}>
                        <Card>
                            <Statistic
                                title="Giá trị đơn hàng trung bình"
                                value={calculateAverageOrderValue()}
                                precision={0}
                                valueStyle={{ color: '#722ed1' }}
                                prefix={<ArrowUpOutlined />}
                                suffix="đ"
                                formatter={(value) =>
                                    `${new Intl.NumberFormat('vi-VN',{ maximumFractionDigits: 0 }).format(Number(value))}`
                                }
                            />
                        </Card>
                    </Col>
                    <Col xs={24} sm={12} md={6}>
                        <Card>
                            <Statistic
                                title="Sản phẩm đã bán"
                                value={calculateTotalProductsSold()}
                                valueStyle={{ color: '#faad14' }}
                                prefix={<TagOutlined />}
                            />
                        </Card>
                    </Col>
                </Row>

                <Divider />

                {/* Biểu đồ doanh thu 7 ngày gần nhất */}
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Card title="Doanh Thu 7 Ngày Gần Nhất">
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={getRevenueByDay()}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="date" />
                                    <YAxis
                                        tickFormatter={(value) =>
                                            `${value / 1000000}tr`
                                        }
                                    />
                                    <Tooltip
                                        formatter={(value) =>
                                            formatCurrency(Number(value))
                                        }
                                    />
                                    <Legend />
                                    <Line
                                        type="monotone"
                                        dataKey="revenue"
                                        stroke="#8884d8"
                                        activeDot={{ r: 8 }}
                                        name="Doanh thu"
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </Card>
                    </Col>
                </Row>

                <Divider />

                {/* Biểu đồ và bảng chi tiết */}
                <Row gutter={[16, 16]}>
                    <Col xs={24} md={12}>
                        <Card title="Đơn Hàng Theo Trạng Thái">
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={getOrdersByStatus()}
                                        cx="50%"
                                        cy="50%"
                                        labelLine={false}
                                        outerRadius={80}
                                        fill="#8884d8"
                                        dataKey="count"
                                        nameKey="status"
                                        label={({ status, count }) =>
                                            `${status}: ${count}`
                                        }
                                    >
                                        {getOrdersByStatus().map(
                                            (entry, index) => (
                                                <Cell
                                                    key={`cell-${index}`}
                                                    fill={
                                                        COLORS[
                                                            index %
                                                                COLORS.length
                                                        ]
                                                    }
                                                />
                                            )
                                        )}
                                    </Pie>
                                    <Tooltip />
                                </PieChart>
                            </ResponsiveContainer>
                        </Card>
                    </Col>
                    <Col xs={24} md={12}>
                        <Card title="Top 5 Sản Phẩm Bán Chạy">
                            <ResponsiveContainer width="100%" height={300}>
                                <BarChart data={getTopSellingProducts()}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis
                                        dataKey="name"
                                        tickFormatter={(value) =>
                                            value.length > 10
                                                ? `${value.substring(0, 10)}...`
                                                : value
                                        }
                                    />
                                    <YAxis />
                                    <Tooltip
                                        formatter={(value, name) =>
                                            name === 'revenue'
                                                ? formatCurrency(Number(value))
                                                : value
                                        }
                                    />
                                    <Legend />
                                    <Bar
                                        dataKey="count"
                                        fill="#0088FE"
                                        name="Số lượng"
                                    />
                                </BarChart>
                            </ResponsiveContainer>
                        </Card>
                    </Col>
                </Row>

                <Divider />

                {/* Danh sách đơn hàng gần đây */}
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Card title="Đơn Hàng Gần Đây">
                            <Table
                                dataSource={getRecentOrders()}
                                columns={orderColumns}
                                rowKey="id"
                                pagination={false}
                            />
                        </Card>
                    </Col>
                </Row>

                <Divider />

                {/* Thống kê chi tiết */}
                <Row gutter={[16, 16]}>
                    <Col xs={24} md={12}>
                        <Card title="Tất Cả Đơn Hàng">
                            <Table
                                dataSource={getFilteredOrders()}
                                columns={orderColumns}
                                rowKey="id"
                                pagination={{ pageSize: 5 }}
                            />
                        </Card>
                    </Col>
                    <Col xs={24} md={12}>
                        <Card title="Thông Tin Khách Hàng">
                            <List
                                itemLayout="horizontal"
                                dataSource={
                                    data
                                        ? Object.values(data.users).slice(0, 5)
                                        : []
                                }
                                renderItem={(user) => (
                                    <List.Item>
                                        <List.Item.Meta
                                            avatar={
                                                <Avatar
                                                    src={user.photoUrl}
                                                    icon={
                                                        !user.photoUrl && (
                                                            <UserOutlined />
                                                        )
                                                    }
                                                />
                                            }
                                            title={user.displayName}
                                            description={user.email}
                                        />
                                        <div>
                                            Tham gia:{' '}
                                            {new Date(
                                                user.creationTimestamp
                                            ).toLocaleDateString('vi-VN')}
                                        </div>
                                    </List.Item>
                                )}
                            />
                        </Card>
                    </Col>
                </Row>
            </Content>
        </div>
    )
}

export default Dashboard
