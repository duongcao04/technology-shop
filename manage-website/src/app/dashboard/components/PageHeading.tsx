import React from 'react'

import { Badge } from 'antd'

type Props = {
    title: string
    count?: number
}

export default function PageHeading({ title, count = 0 }: Props) {
    return (
        <Badge count={count}>
            <h1 className="text-2xl font-bold text-foreground pr-3">{title}</h1>
        </Badge>
    )
}
