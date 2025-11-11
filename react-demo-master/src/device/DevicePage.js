// src/device/DevicePage.js
import React, { useState, useEffect, useContext } from 'react'; // <-- Import useContext
import AuthContext from '../auth/AuthContext'; // <-- Import AuthContext
import * as deviceApi from './device-api'; // <-- Import all as deviceApi
import APIResponseErrorMessage from '../commons/errorhandling/api-response-error-message';
import { Button, Card, CardHeader, Col, Modal, ModalBody, ModalHeader, Row, Table } from 'reactstrap';
// import DeviceForm from './components/DeviceForm'; 

const DevicePage = () => {
    const [devices, setDevices] = useState([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [errorStatus, setErrorStatus] = useState(0);
    
    // --- NEW: Get auth state ---
    const { isAdmin } = useContext(AuthContext); 

    useEffect(() => {
        fetchDevices();
    }, [isAdmin]); // Re-fetch if admin status changes

    function fetchDevices() {
        // --- MODIFIED: Role-based data fetching ---
        const apiCall = isAdmin ? deviceApi.getDevices : deviceApi.getMyDevices;

        apiCall((result, status, err) => {
            if (result !== null && status === 200) {
                setDevices(result);
                setIsLoaded(true);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    }

    const openAddModal = () => console.log('Open Add Modal');
    const openEditModal = (device) => console.log('Open Edit Modal', device);
    const openAssignModal = (device) => console.log('Open Assign Modal', device);

    // --- MODIFIED: Implement delete ---
    const handleDelete = (deviceId) => {
        if (window.confirm('Are you sure you want to delete this device?')) {
            deviceApi.deleteDevice(deviceId, (result, status, err) => {
                if (status === 204) {
                    console.log('Device deleted successfully');
                    fetchDevices(); // Refresh the list
                } else {
                    setErrorStatus(status);
                    setError(err);
                }
            });
        }
    };

    return (
        <div>
            <CardHeader>
                <strong>Device Management</strong>
            </CardHeader>
            <Card>
                <br/>
                {isAdmin && (
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button color="primary" onClick={openAddModal}>Add Device</Button>
                        </Col>
                    </Row>
                )}
                <br/>
                <Row>
                    <Col sm={{ size: '10', offset: 1 }}>
                        {isLoaded ? (
                            <Table responsive striped>
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Status</th>
                                        {isAdmin && <th>Actions</th>}
                                    </tr>
                                </thead>
                                <tbody>
                                    {/* Use deviceName and deviceStatus from DTO */}
                                    {devices.map(device => (
                                        <tr key={device.id}>
                                            <td>{device.deviceName}</td>
                                            <td>{device.deviceStatus}</td>
                                            {isAdmin && (
                                                <td>
                                                    <Button color="info" size="sm" onClick={() => openEditModal(device)}>Edit</Button>{' '}
                                                    <Button color="danger" size="sm" onClick={() => handleDelete(device.id)}>Delete</Button>{' '}
                                                    <Button color="success" size="sm" onClick={() => openAssignModal(device)}>Assign</Button>
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        ) : (
                            <p>Loading devices...</p>
                        )}
                        
                        {errorStatus > 0 && (
                            <APIResponseErrorMessage errorStatus={errorStatus} error={error} />
                        )}
                    </Col>
                </Row>
            </Card>
        </div>
    );
};

export default DevicePage;