import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import ProfileImageWithDefault from './ProfileImageWithDefault';
import { useTranslation } from 'react-i18next';
import { postBook, postBookAttachment } from '../api/apiCalls';
import { useApiProgress } from '../shared/ApiProgress';
import ButtonWithProgress from './ButtonWithProgress';
import Input from './Input';
import AutoUploadImage from './AutoUploadImage';

const BookSubmit = () => {
    const { image } = useSelector(store => ({ image: store.image }));
    const [focused, setFocused] = useState(false);
    const [hoax, setBook] = useState('');
    const [errors, setErrors] = useState({});
    const [newImage, setNewImage] = useState();
    const [attachmentId, setAttachmentId] = useState();
    const { t } = useTranslation();

    useEffect(() => {
        if (!focused) {
            setBook('');
            setErrors({});
            setNewImage();
            setAttachmentId();
        }
    }, [focused]);

    useEffect(() => {
        setErrors({});
    }, [hoax]);

    const pendingApiCall = useApiProgress('post', '/api/books', true);
    const pendingFileUpload = useApiProgress('post', '/api/book-attachments', true);

    const onClickBook = async () => {
        const body = {
            content: hoax,
            attachmentId: attachmentId
        };

        try {
            await postBook(body);
            setFocused(false);
        } catch (error) {
            if (error.response.data.validationErrors) {
                setErrors(error.response.data.validationErrors);
            }
        }
    };

    const onChangeFile = event => {
        if (event.target.files.length < 1) {
            return;
        }
        const file = event.target.files[0];
        const fileReader = new FileReader();
        fileReader.onloadend = () => {
            setNewImage(fileReader.result);
            uploadFile(file);
        };
        fileReader.readAsDataURL(file);
    };

    const uploadFile = async file => {
        const attachment = new FormData();
        attachment.append('file', file);
        const response = await postBookAttachment(attachment);
        setAttachmentId(response.data.id);
    };

    let textAreaClass = 'form-control';
    if (errors.content) {
        textAreaClass += ' is-invalid';
    }

    return (
        <div className="card p-1 flex-row">
            <ProfileImageWithDefault image={image} width="32" height="32" className="rounded-circle mr-1" />
            <div className="flex-fill">
        <textarea
            className={textAreaClass}
            rows={focused ? '3' : '1'}
            onFocus={() => setFocused(true)}
            onChange={event => setBook(event.target.value)}
            value={hoax}
        />
                <div className="invalid-feedback">{errors.content}</div>
                {focused && (
                    <>
                        {!newImage && <Input type="file" onChange={onChangeFile} />}
                        {newImage && <AutoUploadImage image={newImage} uploading={pendingFileUpload} />}
                        <div className="text-right mt-1">
                            <ButtonWithProgress
                                className="btn btn-primary"
                                onClick={onClickBook}
                                text="Book"
                                pendingApiCall={pendingApiCall}
                                disabled={pendingApiCall || pendingFileUpload}
                            />
                            <button className="btn btn-light d-inline-flex ml-1" onClick={() => setFocused(false)} disabled={pendingApiCall || pendingFileUpload}>
                                <i className="material-icons">close</i>
                                {t('Cancel')}
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default BookSubmit;