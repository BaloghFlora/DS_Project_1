// src/user/UserPage.js
import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import {
    Button,
    Card,
    CardHeader,
    Col,
    Modal,
    ModalBody,
    ModalHeader,
    Row
} from 'reactstrap';
import UserForm from "./components/UserForm"; // Renamed
import UserTable from "./components/UserTable"; // Renamed
import * as API_USERS from "./user-api"; // Renamed

class UserPage extends React.Component {

    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reload = this.reload.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
        this.handleEdit = this.handleEdit.bind(this);

        this.state = {
            selected: false,
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null,
            // --- NEW STATE for editing ---
            isEditModalOpen: false,
            editingUser: null
        };
    }

    componentDidMount() {
        this.fetchPersons();
    }

    fetchPersons() {
        return API_USERS.getPersons((result, status, err) => {
            if (result !== null && status === 200) {
                this.setState({
                    tableData: result,
                    isLoaded: true
                });
            } else {
                this.setState(({
                    errorStatus: status,
                    error: err
                }));
            }
        });
    }

    // Toggle Add form
    toggleForm() {
        this.setState({ selected: !this.state.selected });
    }

    // --- NEW: Toggle Edit form ---
    toggleEditForm(user) {
        this.setState({ 
            isEditModalOpen: !this.state.isEditModalOpen,
            editingUser: user || null // Set user if opening, clear if closing
        });
    }
    
    // --- NEW: Handle Delete ---
    handleDelete(userId) {
        if (window.confirm('Are you sure you want to delete this user?')) {
            API_USERS.deleteUser(userId, (result, status, err) => {
                if (status === 204) {
                    console.log("User deleted successfully");
                    this.fetchPersons(); // Refresh table
                } else {
                    this.setState(({
                        errorStatus: status,
                        error: err
                    }));
                }
            });
        }
    }

    // --- NEW: Handle Edit ---
    handleEdit(user) {
        this.toggleEditForm(user);
    }

    reload() {
        this.setState({
            isLoaded: false,
            selected: false, // Close add modal
            isEditModalOpen: false, // Close edit modal
            editingUser: null
        });
        this.fetchPersons();
    }

    render() {
        return (
            <div>
                <CardHeader>
                    <strong> User Management </strong>
                </CardHeader>
                <Card>
                    <br/>
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button color="primary" onClick={this.toggleForm}>Add User</Button>
                        </Col>
                    </Row>
                    <br/>
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            {this.state.isLoaded && 
                                <UserTable 
                                    tableData={this.state.tableData}
                                    // --- NEW: Pass handlers to table ---
                                    onEdit={this.handleEdit}
                                    onDelete={this.handleDelete}
                                />
                            }
                            {this.state.errorStatus > 0 && <APIResponseErrorMessage
                                                            errorStatus={this.state.errorStatus}
                                                            error={this.state.error}
                                                        />   }
                        </Col>
                    </Row>
                </Card>

                {/* --- ADD MODAL --- */}
                <Modal isOpen={this.state.selected} toggle={this.toggleForm}
                       className={this.props.className} size="lg">
                    <ModalHeader toggle={this.toggleForm}> Add User: </ModalHeader>
                    <ModalBody>
                        <UserForm reloadHandler={this.reload}/>
                    </ModalBody>
                </Modal>

                {/* --- NEW: EDIT MODAL --- */}
                {this.state.isEditModalOpen && (
                    <Modal isOpen={this.state.isEditModalOpen} toggle={() => this.toggleEditForm(null)}
                        className={this.props.className} size="lg">
                        <ModalHeader toggle={() => this.toggleEditForm(null)}> Edit User: </ModalHeader>
                        <ModalBody>
                            <UserForm 
                                reloadHandler={this.reload}
                                // Pass the user to the form
                                user={this.state.editingUser} 
                            />
                        </ModalBody>
                    </Modal>
                )}
            </div>
        )
    }
}

export default UserPage;