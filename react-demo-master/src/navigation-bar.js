// src/navigation-bar.js
import React from 'react';
import logo from './commons/images/icon.png';
import {
    Nav,
    Navbar,
    NavbarBrand,
    NavLink,
    Button
} from 'reactstrap';
import { useAuth } from './auth/AuthContext';
import { Link } from 'react-router-dom';

const NavigationBar = () => {
    const { user, isAdmin, logout } = useAuth();

    return (
        <div>
            <Navbar color="dark" light expand="md">
                <NavbarBrand tag={Link} to="/">
                    <img src={logo} width={"50"} height={"35"} alt="logo" />
                </NavbarBrand>
                <Nav className="mr-auto" navbar>
                    {user && (
                        <>
                            <NavLink tag={Link} to="/devices" style={{ color: 'white' }}>Devices</NavLink>
                            {/* Add Monitoring Link */}
                            <NavLink tag={Link} to="/monitoring" style={{ color: 'white' }}>My Energy</NavLink>
                        </>
                    )}
                    {isAdmin && (
                        <NavLink tag={Link} to="/users" style={{ color: 'white' }}>Users</NavLink>
                    )}
                </Nav>
                <Nav className="ms-auto" navbar>
                    {user ? (
                        <Button color="primary" onClick={logout}>Logout</Button>
                    ) : (
                        <Button color="primary" tag={Link} to="/login">Login</Button>
                    )}
                </Nav>
            </Navbar>
        </div>
    );
};

export default NavigationBar;