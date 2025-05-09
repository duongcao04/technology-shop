import { NextResponse } from 'next/server';
import { firebaseService } from '@/lib/firebase/services';

export async function GET() {
	try {
		const users = await firebaseService.getAll('users');
		return NextResponse.json(users);
	} catch (error: unknown) {
		return NextResponse.json({ error: `Failed to fetch customers: ${error}` }, { status: 500 });
	}
}

export async function POST(request: Request) {
	try {
		const user = await request.json();
		const id = await firebaseService.push('users', user);
		return NextResponse.json({ id, success: true });
	} catch (error: unknown) {
		return NextResponse.json({ error: `Failed to add customer: ${error}` }, { status: 500 });
	}
}