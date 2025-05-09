import { Product } from "../products/type"

export type Order = {
	id: string,
	description: string,
	discountPercent: string | number,
	imageUrl: string,
	name: string,
	price: string
	status: string
	orderDate: string
	totalPrice: string
	phone: string
	items: OrderItem
}

export type OrderItem = {
	product: Product
}