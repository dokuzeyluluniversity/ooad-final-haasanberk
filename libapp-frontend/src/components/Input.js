import React from "react";


const Input = (props) => {
    const {label, error, name, onChange, type} = props;
    let className = 'form-control';


    if (error !== undefined) {
        className += ' is-invalid';
    }

    return (
        <div className="form-label-group">
            <input type={type}
                   className={className}
                   name={name}
                   onChange={onChange}
                   autoFocus required/>
            <div className="invalid-feedback">
                {error}
            </div>
            <label>{label}</label>
        </div>
    );
}

export default Input;