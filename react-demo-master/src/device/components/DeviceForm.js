// src/device/components/DeviceForm.js
import React from 'react';
import validate from "../../user/components/validators/user-validators.js"; 
import Button from "react-bootstrap/Button";
import * as API_DEVICES from "../device-api.js"; 
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import {Col, Row} from "reactstrap";
import { FormGroup, Input, Label} from 'reactstrap';

class DeviceForm extends React.Component {

    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reloadHandler = this.props.reloadHandler;
        
        this.isEdit = this.props.device ? true : false;
        const device = this.props.device;

        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: this.isEdit,
            formControls: {
                deviceName: {
                    value: device ? device.deviceName : '',
                    placeholder: 'Device Name...',
                    valid: this.isEdit,
                    touched: this.isEdit,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                deviceStatus: {
                    value: device ? device.deviceStatus : '',
                    placeholder: 'Status (e.g., ACTIVE)...',
                    valid: this.isEdit,
                    touched: this.isEdit,
                    validationRules: {
                        isRequired: true
                    }
                },
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        
        // [!code ++]
        // --- THIS IS THE FIX ---
        // You must bind all methods that are called by other methods
        this.registerDevice = this.registerDevice.bind(this);
        this.updateDevice = this.updateDevice.bind(this);
        // [!code --]
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

    // --- This function exists here ---
    registerDevice(device) {
        // The backend DTO requires a userIds array
        // We send an empty one on creation.
        const payload = { ...device, userIds: [] }; 
        
        return API_DEVICES.postDevice(payload, (result, status, error) => { 
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully inserted device");
                this.reloadHandler();
            } else {
                this.setState(({
                    errorStatus: status,
                    error: error
                }));
            }
        });
    }

    updateDevice(deviceId, device) {
        return API_DEVICES.updateDevice(deviceId, device, (result, status, error) => {
            if (result !== null && status === 200) {
                console.log("Successfully updated device with id: " + result.id);
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
        let device = {
            deviceName: this.state.formControls.deviceName.value,
            deviceStatus: this.state.formControls.deviceStatus.value,
        };

        if (this.isEdit) {
            device.id = this.props.device.id; 
            device.userIds = this.props.device.userIds || []; 
            this.updateDevice(this.props.device.id, device);
        } else {
            // --- And is called here ---
            this.registerDevice(device); 
        }
    }

    render() {
        return (
            <div>
                <FormGroup id='deviceName'>
                    <Label for='deviceNameField'> Name: </Label>
                    <Input name='deviceName' id='deviceNameField' placeholder={this.state.formControls.deviceName.placeholder}
                           onChange={this.handleChange}
                           value={this.state.formControls.deviceName.value}
                           touched={this.state.formControls.deviceName.touched? 1 : 0}
                           valid={this.state.formControls.deviceName.valid}
                           required
                    />
                    {this.state.formControls.deviceName.touched && !this.state.formControls.deviceName.valid &&
                    <div className={"error-message row"}> * Name must have at least 3 characters </div>}
                </FormGroup>

                <FormGroup id='deviceStatus'>
                    <Label for='deviceStatusField'> Status: </Label>
                    <Input name='deviceStatus' id='deviceStatusField' placeholder={this.state.formControls.deviceStatus.placeholder}
                           onChange={this.handleChange}
                           value={this.state.formControls.deviceStatus.value}
                           touched={this.state.formControls.deviceStatus.touched? 1 : 0}
                           valid={this.state.formControls.deviceStatus.valid}
                           required
                    />
                    {this.state.formControls.deviceStatus.touched && !this.state.formControls.deviceStatus.valid &&
                    <div className={"error-message"}> * Status is required</div>}
                </FormGroup>

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

export default DeviceForm;