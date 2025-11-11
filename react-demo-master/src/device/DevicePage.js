// src/device/DevicePage.js
import React, { useState, useEffect, useContext } from 'react'; 
import AuthContext from '../auth/AuthContext'; 
import * as deviceApi from './device-api'; 
import * as userApi from '../user/user-api.js';
import APIResponseErrorMessage from '../commons/errorhandling/api-response-error-message';
import { Button, Card, CardHeader, Col, Modal, ModalBody, ModalHeader, Row, Table,
         FormGroup, Label, Input 
} from 'reactstrap';
import DeviceForm from './components/DeviceForm'; 

const DevicePage = () => {
    const [devices, setDevices] = useState([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [errorStatus, setErrorStatus] = useState(0);
    
    const { isAdmin } = useContext(AuthContext); 

    // --- State for Modals --- 
    const [isAddModalOpen, setIsAddModalOpen] = useState(false); 
    const [isEditModalOpen, setIsEditModalOpen] = useState(false); 
    const [isAssignModalOpen, setIsAssignModalOpen] = useState(false); 
    
    const [editingDevice, setEditingDevice] = useState(null); // For Edit modal 
    const [assigningDevice, setAssigningDevice] = useState(null); // For Assign modal 
    
    // State for Assign modal's user list 
    const [users, setUsers] = useState([]); 
    const [selectedUserId, setSelectedUserId] = useState(''); 
    // --- End State for Modals --- 

    useEffect(() => {
        fetchDevices();
        if (isAdmin) { 
            fetchUsers(); 
        } 
    }, [isAdmin]); 

    function fetchDevices() {
        const apiCall = isAdmin ? deviceApi.getDevices : deviceApi.getMyDevices;

        apiCall((result, status, err) => {
            if (result !== null && status === 200) {
                setDevices(result);
                setIsLoaded(true);
                setError(null); 
                setErrorStatus(0); 
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    }

    // --- Fetch Users for Assign/Create ---
    function fetchUsers() {
        deviceApi.getUsers((result, status, err) => {
            if (result !== null && status === 200) {
                setUsers(result);
            } else {
                console.error("Failed to fetch users:", err);
                if (!isAssignModalOpen) {
                    setErrorStatus(status);
                    setError(err);
                }
            }
        });
    }

    // --- Modal Toggles ---
    const toggleAddModal = () => {
        setIsAddModalOpen(!isAddModalOpen); 
    }
    
    const toggleEditModal = (device) => {
        if (device) setEditingDevice(device); 
        setIsEditModalOpen(!isEditModalOpen); 
    };

    const toggleAssignModal = (device) => {
        if (device) {
            setAssigningDevice(device);
            setSelectedUserId(''); 
            setError(null); 
            setErrorStatus(0); 
        }
        setIsAssignModalOpen(!isAssignModalOpen);
    };
    // --- End Modal Toggles ---

    const handleDelete = (deviceId) => {
        if (window.confirm('Are you sure you want to delete this device?')) {
            deviceApi.deleteDevice(deviceId, (result, status, err) => {
                if (status === 204) {
                    console.log('Device deleted successfully');
                    fetchDevices(); 
                } else {
                    setErrorStatus(status);
                    setError(err);
                }
            });
        }
    };
    
    // --- Handle Assign Submit ---
    const handleAssignSubmit = () => {
        if (!assigningDevice || !selectedUserId) {
            console.error("No device or user selected");
            return;
        }
        
        deviceApi.assignDevice(assigningDevice.id, selectedUserId, (result, status, err) => {
            if (status === 200) {
                console.log("User assigned successfully");
                toggleAssignModal(null); 
                fetchDevices(); 
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    // [!code ++]
    // --- Smarter Sync Function ---
    const syncUsers = () => {
        console.log("Starting user sync...");

        // 1. Get all users from the user-service (Person table)
        userApi.getPersons((allPeople, status, err) => {
            if (err) {
                alert("Failed to fetch users from user-service: " + err);
                return;
            }

            // 2. Get all users already in the device-service (User table)
            deviceApi.getUsers((deviceUsers, status, err) => {
                if (err) {
                    alert("Failed to fetch users from device-service: " + err);
                    return;
                }

                // Create a list of usernames that are already in the device-service
                const existingUsernames = deviceUsers.map(u => u.username);
                
                // 3. Find which users are missing
                const missingUsers = allPeople.filter(person => 
                    !existingUsernames.includes(person.fullName)
                );

                if (missingUsers.length === 0) {
                    alert("All users are already in sync!");
                    fetchUsers(); // Refresh dropdown just in case
                    return;
                }

                console.log("Found missing users to sync:", missingUsers);

                // 4. Create a POST request for each missing user
                const promises = missingUsers.map(person => {
                    const deviceUser = { username: person.fullName };
                    return new Promise((resolve, reject) => {
                        deviceApi.postUser(deviceUser, (res, status, err) => {
                            if (err) {
                                console.error("Failed to sync user " + person.fullName, err);
                                reject(err);
                            } else {
                                console.log("Synced user: " + person.fullName);
                                resolve(res);
                            }
                        });
                    });
                });

                // 5. After all posts are done, show alert and refresh
                Promise.all(promises)
                    .then(() => {
                        alert(`Successfully synced ${missingUsers.length} user(s). Please re-open the 'Assign' modal.`);
                        fetchUsers(); // Refresh the dropdown
                    })
                    .catch(e => {
                        alert("An error occurred during sync. Check console.");
                        fetchUsers(); // Still refresh
                    });
            });
        });
    };
    // [!code --]

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
                            <Button color="primary" onClick={toggleAddModal}>Add Device</Button>
                            <Button color="warning" onClick={syncUsers} style={{marginLeft: '10px'}}>
                                Sync Users (Debug)
                            </Button>
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
                                    {devices.map(device => (
                                        <tr key={device.id}>
                                            <td>{device.deviceName}</td>
                                            <td>{device.deviceStatus}</td>
                                            {isAdmin && (
                                                <td>
                                                    <Button color="info" size="sm" onClick={() => toggleEditModal(device)}>Edit</Button>{' '} 
                                                    <Button color="danger" size="sm" onClick={() => handleDelete(device.id)}>Delete</Button>{' '} 
                                                    <Button color="success" size="sm" onClick={() => toggleAssignModal(device)}>Assign</Button> 
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        ) : (
                            <p>Loading devices...</p>
                        )}
                        
                        {errorStatus > 0 && !isAssignModalOpen && !isAddModalOpen && !isEditModalOpen && ( 
                            <APIResponseErrorMessage errorStatus={errorStatus} error={error} /> 
                        )}
                    </Col>
                </Row>
            </Card>

            {/* --- ADD MODAL --- */}
            <Modal isOpen={isAddModalOpen} toggle={toggleAddModal} size="lg">
                <ModalHeader toggle={toggleAddModal}> Add Device: </ModalHeader>
                <ModalBody>
                    <DeviceForm reloadHandler={fetchDevices} />
                </ModalBody>
            </Modal>
            
            {/* --- EDIT MODAL --- */}
            {isEditModalOpen && ( 
                <Modal isOpen={isEditModalOpen} toggle={() => toggleEditModal(null)} size="lg">
                    <ModalHeader toggle={() => toggleEditModal(null)}> Edit Device: </ModalHeader>
                    <ModalBody>
                        <DeviceForm reloadHandler={fetchDevices} device={editingDevice} />
                    </ModalBody>
                </Modal>
            )}
            
            {/* --- ASSIGN MODAL (Implemented) --- */}
            {isAssignModalOpen && (
                <Modal isOpen={isAssignModalOpen} toggle={() => toggleAssignModal(null)} size="lg">
                    <ModalHeader toggle={() => toggleAssignModal(null)}> 
                        Assign User to: {assigningDevice?.deviceName}
                    </ModalHeader>
                    <ModalBody>
                        <FormGroup>
                            <Label for="userSelect">User</Label>
                            <Input
                                type="select"
                                name="user"
                                id="userSelect"
                                value={selectedUserId}
                                onChange={(e) => setSelectedUserId(e.target.value)}
                            >
                                <option value="">Select a user...</option>
                                {users.map(user => (
                                    <option key={user.id} value={user.id}>
                                        {user.username}
                                    </option>
                                ))}
                            </Input>
                        </FormGroup>
                        <Button 
                            color="primary" 
                            onClick={handleAssignSubmit} 
                            disabled={!selectedUserId}
                        >
                            Assign User
                        </Button>
                        
                        {errorStatus > 0 && (
                            <APIResponseErrorMessage errorStatus={errorStatus} error={error} />
                        )}
                    </ModalBody>
                </Modal>
            )}
        </div>
    );
};

export default DevicePage;