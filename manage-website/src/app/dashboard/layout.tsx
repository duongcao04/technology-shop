import { cn } from '@/lib/utils'

import ManageSidebar from './components/ManageSidebar'

export default function ManageLayout({
    children,
}: {
    children: React.ReactNode
}) {
    return (
        <div
            className={cn(
                'max-w-screen max-h-screen w-screen h-screen mx-auto flex flex-1 flex-col overflow-hidden rounded-md border border-neutral-200 bg-gray-100 md:flex-row dark:border-neutral-700 dark:bg-neutral-800'
            )}
        >
            <ManageSidebar />
            {children}
        </div>
    )
}
