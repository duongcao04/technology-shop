import { NextResponse } from 'next/server';
import { firebaseService } from '@/lib/firebase/services';

export async function GET() {
	try {
		const products = await firebaseService.getAll('products');
		return NextResponse.json(products);
	} catch (error: unknown) {
		return NextResponse.json({ error: `Failed to fetch products: ${error}` }, { status: 500 });
	}
}

export async function POST(request: Request) {
	try {
		const product = await request.json();
		const id = await firebaseService.push('products', product);
		return NextResponse.json({ id, success: true });
	} catch (error: unknown) {
		return NextResponse.json({ error: `Failed to add product: ${error}` }, { status: 500 });
	}
}

export async function DELETE(request: Request) {
	try {
		const body = await request.json(); // Properly parse the request body
		await firebaseService.removeById('products', body.productId); // Access the 'id' property
		return NextResponse.json({ success: true, message: 'Product removed successfully' });
	} catch (error: unknown) {
		const errorMessage = error instanceof Error ? error.message : 'Unknown error';
		return NextResponse.json(
			{ error: `Failed to remove product: ${errorMessage}` },
			{ status: 500 }
		);
	}
}
