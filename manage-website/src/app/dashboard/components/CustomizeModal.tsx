'use clinet'

import React from 'react'

import { Modal, ModalProps } from 'antd'

interface IProps extends ModalProps {
    title?: string | 'Modal'
}

type Props = {
    children: React.ReactNode
    trigger: React.ReactNode
    props?: IProps
    isModalOpen: boolean
}
export default function CustomizeModal({
    children,
    trigger,
    props,
    isModalOpen,
}: Props) {
    return (
        <>
            <div>{trigger}</div>
            <Modal {...props} open={isModalOpen}>
                {children}
            </Modal>
        </>
    )
}
