// src/app.js
import React from 'react'
import { Route, Routes } from 'react-router-dom'
import NavigationBar from './navigation-bar'
import Home from './home/home';
import ErrorPage from './commons/errorhandling/error-page';
import styles from './commons/styles/project-style.css';

import LoginPage from './auth/LoginPage';
import UserPage from './user/UserPage';
import DevicePage from './device/DevicePage';
import MonitoringPage from './monitoring/MonitoringPage'; // 1. Import
import ProtectedRoute from './components/ProtectedRoute';

class App extends React.Component {
    render() {
        return (
            <div className={styles.back}>
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
                    {/* 2. Add Monitoring Route */}
                    <Route 
                        path="/monitoring"
                        element={
                            <ProtectedRoute>
                                <MonitoringPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route path="/error" element={<ErrorPage />} />
                    <Route path="*" element={<ErrorPage />} />
                </Routes>
            </div>
        )
    };
}

export default App;