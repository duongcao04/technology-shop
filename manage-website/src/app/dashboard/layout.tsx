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
            <div className="flex flex-1">
                <div className="flex h-full w-full flex-1 flex-col gap-2 rounded-tl-2xl border border-neutral-200 bg-white p-2 md:px-10 md:py-7 dark:border-neutral-700 dark:bg-neutral-900">
                    {children}
                </div>
            </div>
        </div>
    )
}
