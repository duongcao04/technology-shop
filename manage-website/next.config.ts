import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [new URL('https://assets.aceternity.com/manu.png')],
    },
    eslint: {
        // Tắt ESLint trong quá trình build
        ignoreDuringBuilds: true,
    },
    typescript: {
        // Tắt TypeScript type checking trong quá trình build
        ignoreBuildErrors: true,
    }
}

export default nextConfig
