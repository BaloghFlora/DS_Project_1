// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import * as serviceWorker from './serviceWorker';
import App from './app';
import { AuthProvider } from './auth/AuthContext';
import { BrowserRouter as Router } from 'react-router-dom'; // <-- 1. IMPORT ROUTER HERE

import 'bootstrap/dist/css/bootstrap.min.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <Router> {/* <--- 2. WRAP EVERYTHING WITH ROUTER */}
            <AuthProvider>
                <App />
            </AuthProvider>
        </Router>
    </React.StrictMode>
);

serviceWorker.unregister();