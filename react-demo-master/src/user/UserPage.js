import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import {
    Button,
    Card,
    CardHeader,
    CardBody,
    CardTitle,
    CardText,
    Col,
    Modal,
    ModalBody,
    ModalHeader,
    Row
} from 'reactstrap';
import UserForm from "./components/UserForm";
import * as API_USERS from "./user-api";

class UserPage extends React.Component {

    constructor(props) {
        super(props);
        this.toggleAddForm = this.toggleAddForm.bind(this);
        this.toggleEditForm = this.toggleEditForm.bind(this);
        this.reload = this.reload.bind(this);
        this.handleDelete = this.handleDelete.bind(this);

        this.state = {
            users: [], // Renamed from tableData
            isLoaded: false,
            errorStatus: 0,
            error: null,
            isAddModalOpen: false,
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
                    users: result, // Set users
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

    handleDelete(userId) {
        if (window.confirm('Are you sure you want to delete this user?')) {
            API_USERS.deleteUser(userId, (result, status, err) => {
                if (status === 204) {
                    console.log("User deleted successfully");
                    this.fetchPersons(); // Refresh list
                } else {
                    this.setState(({
                        errorStatus: status,
                        error: err
                    }));
                }
            });
        }
    }
    
    toggleAddForm() {
        this.setState({ isAddModalOpen: !this.state.isAddModalOpen });
    }

    toggleEditForm(user) {
        this.setState({ 
            isEditModalOpen: !this.state.isEditModalOpen,
            editingUser: user || null 
        });
    }

    reload() {
        this.setState({
            isLoaded: false,
            isAddModalOpen: false,
            isEditModalOpen: false,
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
                            <Button color="primary" onClick={this.toggleAddForm}>Add User</Button>
                        </Col>
                    </Row>
                    <br/>
                    <Row>
                        <Col sm={{ size: '10', offset: 1 }}>
                            {this.state.isLoaded && this.state.users.map(user => (
                                <Card body key={user.id} style={{ marginBottom: '10px' }}>
                                    <Row>
                                        <Col xs="8">
                                            <CardTitle tag="h5">{user.fullName}</CardTitle>
                                            <CardText>{user.email}</CardText>
                                        </Col>
                                        <Col xs="4" style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                                            <Button 
                                                color="info" 
                                                size="sm" 
                                                onClick={() => this.toggleEditForm(user)}
                                                style={{ marginRight: '10px' }}
                                            >
                                                Edit
                                            </Button>
                                            <Button 
                                                color="danger" 
                                                size="sm" 
                                                onClick={() => this.handleDelete(user.id)}
                                            >
                                                Delete
                                            </Button>
                                        </Col>
                                    </Row>
                                </Card>
                            ))}
                            
                            {this.state.errorStatus > 0 && <APIResponseErrorMessage
                                                            errorStatus={this.state.errorStatus}
                                                            error={this.state.error}
                                                        />   }
                        </Col>
                    </Row>
                    <br/>
                </Card>

                {/* --- ADD MODAL --- */}
                <Modal isOpen={this.state.isAddModalOpen} toggle={this.toggleAddForm}
                       className={this.props.className} size="lg">
                    <ModalHeader toggle={this.toggleAddForm}> Add User: </ModalHeader>
                    <ModalBody>
                        <UserForm reloadHandler={this.reload}/>
                    </ModalBody>
                </Modal>

                {/* --- EDIT MODAL --- */}
                {this.state.isEditModalOpen && (
                    <Modal isOpen={this.state.isEditModalOpen} toggle={() => this.toggleEditForm(null)}
                        className={this.props.className} size="lg">
                        <ModalHeader toggle={() => this.toggleEditForm(null)}> Edit User: </ModalHeader>
                        <ModalBody>
                            <UserForm 
                                reloadHandler={this.reload}
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