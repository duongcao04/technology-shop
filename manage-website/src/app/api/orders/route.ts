import { NextResponse } from 'next/server';
import { firebaseService } from '@/lib/firebase/services';

export async function GET() {
	try {
		const orders = await firebaseService.getAll('orders');
		return NextResponse.json(orders);
	} catch (error: unknown) {
		return NextResponse.json({ error: `Failed to fetch orders: ${error}` }, { status: 500 });
	}
}