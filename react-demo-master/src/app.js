// src/app.js
import React from 'react'
// --- 1. REMOVE 'BrowserRouter as Router' from this import ---
import { Route, Routes } from 'react-router-dom' 
import NavigationBar from './navigation-bar'
import Home from './home/home';
import ErrorPage from './commons/errorhandling/error-page';
import styles from './commons/styles/project-style.css';

import LoginPage from './auth/LoginPage';
import UserPage from './user/UserPage';
import DevicePage from './device/DevicePage';
import ProtectedRoute from './components/ProtectedRoute';

class App extends React.Component {
    render() {
        return (
            <div className={styles.back}>
                {/* --- 2. REMOVE <Router> TAGS FROM HERE --- */}
                <NavigationBar />
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<Home />} />
                    
                    <Route 
                        path="/users"
                        element={
                            <ProtectedRoute adminOnly={true}>
                                <UserPage />
                            </ProtectedRoute>
                        } 
                    />
                    <Route 
                        path="/devices"
                        element={
                            <ProtectedRoute>
                                <DevicePage />
                            </ProtectedRoute>
                        }
                    />
                    <Route path="/error" element={<ErrorPage />} />
                    <Route path="*" element={<ErrorPage />} />
                </Routes>
                {/* --- 2. REMOVE </Router> TAGS FROM HERE --- */}
            </div>
        )
    };
}

export default App;