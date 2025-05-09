import { NextResponse } from 'next/server';
import cloudinary from '@/lib/cloudinary';

export async function POST(request: Request) {
	try {
		// Lấy dữ liệu từ body
		const formData = await request.formData();
		const file = formData.get('file') as File;

		if (!file) {
			return NextResponse.json({ error: 'Không tìm thấy file' }, { status: 400 });
		}

		// Chuyển đổi file sang Blob
		const fileBuffer = await file.arrayBuffer();
		const fileBase64 = Buffer.from(fileBuffer).toString('base64');
		const fileData = `data:${file.type};base64,${fileBase64}`;

		// Upload lên Cloudinary
		const response = await cloudinary.uploader.upload(fileData, {
			folder: 'techshop',
		});

		return NextResponse.json({ url: response.secure_url });
	} catch (error: unknown) {
		return NextResponse.json({ error: error }, { status: 500 });
	}
}
