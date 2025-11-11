// src/user/components/UserForm.js
import React from 'react';
import validate from "./validators/user-validators"; // Renamed
import Button from "react-bootstrap/Button";
import * as API_USERS from "../user-api"; // Renamed
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import {Col, Row} from "reactstrap";
import { FormGroup, Input, Label} from 'reactstrap';

class UserForm extends React.Component {

    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reloadHandler = this.props.reloadHandler;
        
        // --- NEW: Check if we are editing ---
        this.isEdit = this.props.user ? true : false;
        const user = this.props.user;

        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: this.isEdit, // Form is valid on edit
            formControls: {
                name: {
                    value: user ? user.fullName : '', // Use fullName
                    placeholder: 'What is your name?...',
                    valid: this.isEdit,
                    touched: this.isEdit,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                email: {
                    value: user ? user.email : '',
                    placeholder: 'Email...',
                    valid: this.isEdit,
                    touched: this.isEdit,
                    validationRules: {
                        emailValidator: true
                    }
                },
                // --- NEW: Add password field ---
                password: {
                    value: '', // Don't prefill password
                    placeholder: this.isEdit ? 'New Password (optional)...' : 'Password...',
                    valid: this.isEdit, // Valid if empty on edit
                    touched: false,
                    validationRules: {
                        // Only required when creating a new user
                        isRequired: !this.isEdit 
                    }
                },
                // Note: The DTOs in user-service don't have age or address
                // I am removing them to match the backend
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    toggleForm() {
        this.setState({collapseForm: !this.state.collapseForm});
    }

    handleChange = event => {
        const name = event.target.name;
        const value = event.target.value;
        const updatedControls = this.state.formControls;
        const updatedFormElement = updatedControls[name];

        updatedFormElement.value = value;
        updatedFormElement.touched = true;
        updatedFormElement.valid = validate(value, updatedFormElement.validationRules);
        
        // --- NEW: Special rule for password on edit ---
        if (this.isEdit && name === 'password' && value === '') {
             updatedFormElement.valid = true; // Empty password is ok on edit
        }

        updatedControls[name] = updatedFormElement;

        let formIsValid = true;
        for (let updatedFormElementName in updatedControls) {
            formIsValid = updatedControls[updatedFormElementName].valid && formIsValid;
        }

        this.setState({
            formControls: updatedControls,
            formIsValid: formIsValid
        });
    };

    registerPerson(person) {
        return API_USERS.postPerson(person, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully inserted person with id: " + result);
                this.reloadHandler();
            } else {
                this.setState(({
                    errorStatus: status,
                    error: error
                }));
            }
        });
    }

    // --- NEW: Update Function ---
    updatePerson(userId, person) {
        return API_USERS.updateUser(userId, person, (result, status, error) => {
            if (result !== null && status === 200) {
                console.log("Successfully updated person with id: " + result.id);
                this.reloadHandler();
            } else {
                this.setState(({
                    errorStatus: status,
                    error: error
                }));
            }
        });
    }

    handleSubmit() {
        let person = {
            fullName: this.state.formControls.name.value,
            email: this.state.formControls.email.value,
            password: this.state.formControls.password.value,
        };

        // --- NEW: Check if editing or creating ---
        if (this.isEdit) {
            // If password field is empty, don't send it
            if (person.password === "") {
                const { password, ...personWithoutPassword } = person;
                // You MUST update your backend DTO/Service to handle null passwords on update
                // For now, we'll send the original password back
                person.password = this.props.user.password; 
            }
            this.updatePerson(this.props.user.id, person);
        } else {
            this.registerPerson(person);
        }
    }

    render() {
        return (
            <div>
                <FormGroup id='name'>
                    <Label for='nameField'> Name: </Label>
                    <Input name='name' id='nameField' placeholder={this.state.formControls.name.placeholder}
                           onChange={this.handleChange}
                           defaultValue={this.state.formControls.name.value}
                           touched={this.state.formControls.name.touched? 1 : 0}
                           valid={this.state.formControls.name.valid}
                           required
                    />
                    {this.state.formControls.name.touched && !this.state.formControls.name.valid &&
                    <div className={"error-message row"}> * Name must have at least 3 characters </div>}
                </FormGroup>

                <FormGroup id='email'>
                    <Label for='emailField'> Email: </Label>
                    <Input name='email' id='emailField' placeholder={this.state.formControls.email.placeholder}
                           onChange={this.handleChange}
                           defaultValue={this.state.formControls.email.value}
                           touched={this.state.formControls.email.touched? 1 : 0}
                           valid={this.state.formControls.email.valid}
                           required
                    />
                    {this.state.formControls.email.touched && !this.state.formControls.email.valid &&
                    <div className={"error-message"}> * Email must have a valid format</div>}
                </FormGroup>

                {/* --- NEW: Password Field --- */}
                <FormGroup id='password'>
                    <Label for='passwordField'> Password: </Label>
                    <Input type="password" name='password' id='passwordField' 
                           placeholder={this.state.formControls.password.placeholder}
                           onChange={this.handleChange}
                           defaultValue={this.state.formControls.password.value}
                           touched={this.state.formControls.password.touched? 1 : 0}
                           valid={this.state.formControls.password.valid}
                           required={!this.isEdit} // Only required on create
                    />
                    {this.state.formControls.password.touched && !this.state.formControls.password.valid &&
                    <div className={"error-message"}> * Password is required</div>}
                </FormGroup>
                
                {/* Removed Age and Address fields */}

                <Row>
                    <Col sm={{size: '4', offset: 8}}>
                        <Button type={"submit"} disabled={!this.state.formIsValid} onClick={this.handleSubmit}>  Submit </Button>
                    </Col>
                </Row>

                {
                    this.state.errorStatus > 0 &&
                    <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error}/>
                }
            </div>
        ) ;
    }
}

export default UserForm;