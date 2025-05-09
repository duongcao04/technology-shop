import { initializeApp, getApps } from 'firebase/app';
import firebaseConfig from '@/config/firebaseConfig';
import { getDatabase } from 'firebase/database';

export const app = !getApps().length ? initializeApp(firebaseConfig) : getApps()[0];
export const database = getDatabase(app);