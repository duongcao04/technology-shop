import { getAnalytics } from 'firebase/analytics'
import { initializeApp } from 'firebase/app'
import { getAuth } from 'firebase/auth'
import { getFirestore } from 'firebase/firestore'

import firebaseConfig from '@/config/firebaseConfig'

export const app = initializeApp(firebaseConfig)
export const analytics = getAnalytics(app)

export const auth = getAuth
export const firestore = getFirestore
