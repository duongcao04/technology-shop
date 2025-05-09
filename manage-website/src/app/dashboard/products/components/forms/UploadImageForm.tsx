import React from 'react'

import { InboxOutlined } from '@ant-design/icons'
import { IconTrash } from '@tabler/icons-react'
import { Button, Form, Image } from 'antd'
import { Upload, message } from 'antd'
import { RcFile, UploadProps } from 'antd/es/upload'
import { FormikProps } from 'formik'

import { Product } from '../../../../api/products/type'

const { Dragger } = Upload

const beforeUpload = (file: RcFile) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
    if (!isJpgOrPng) {
        message.error('You can only upload JPG/PNG file!')
    }
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
        message.error('Image must be smaller than 2MB!')
    }
    return isJpgOrPng && isLt2M
}

export default function UploadImageForm({
    formik,
}: {
    formik: FormikProps<Product>
}) {
    const props: UploadProps = {
        name: 'file',
        multiple: false,
        beforeUpload: beforeUpload,
        action: '/api/upload',
        onChange(info) {
            const { status } = info.file
            if (status !== 'uploading') {
                console.log(info.file, info.fileList)
            }
            if (status === 'done') {
                if (info.file.response && info.file.response.url) {
                    formik.setFieldValue('imageUrl', info.file.response.url)
                }
                message.success(`${info.file.name} file uploaded successfully.`)
            } else if (status === 'error') {
                message.error(`${info.file.name} file upload failed.`)
            }
        },
        onDrop(e) {
            console.log('Dropped files', e.dataTransfer.files)
        },
    }

    return (
        <Form.Item
            name="imageUrl"
            rules={[{ required: true, message: 'Vui lòng chọn ảnh!' }]}
        >
            {!formik.values.imageUrl && (
                <Dragger {...props}>
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined />
                    </p>
                    <p className="ant-upload-text">
                        Nhấp hoặc kéo tệp vào khu vực này để tải lên
                    </p>
                    <p className="ant-upload-hint">
                        Hỗ trợ tải lên một lần hoặc hàng loạt. Nghiêm cấm tải
                        lên dữ liệu công ty hoặc các tệp bị cấm khác.
                    </p>
                </Dragger>
            )}
            {Boolean(formik.errors.imageUrl) && (
                <p className="text-left text-sm text-red-500">
                    {formik.errors.imageUrl}
                </p>
            )}
            {formik.values.imageUrl && (
                <div className="relative w-full h-full flex items-center justify-center">
                    <Image
                        src={formik.values.imageUrl}
                        alt={formik.values.name}
                        className="w-full h-full object-contain"
                    />
                    <div className="absolute top-5 right-5">
                        <Button
                            icon={<IconTrash />}
                            type="primary"
                            danger
                            classNames={{
                                icon: 'flex items-center justify-center',
                            }}
                            onClick={() => {
                                formik.setFieldValue('imageUrl', '')
                            }}
                        />
                    </div>
                </div>
            )}
        </Form.Item>
    )
}
