import React, { useState, useEffect } from 'react';
import { getBooks, getOldBooks, getNewBookCount, getNewBooks } from '../api/apiCalls';
import { useTranslation } from 'react-i18next';
import BookView from './BookView';
import { useApiProgress } from '../shared/ApiProgress';
import Spinner from './Spinner';
import { useParams } from 'react-router-dom';

const BookFeed = () => {
    const [bookPage, setBookPage] = useState({ content: [], last: true, number: 0 });
    const [newBookCount, setNewBookCount] = useState(0);
    const { t } = useTranslation();
    const { username } = useParams();

    const path = username ? `/api/books?page=${username.id}` : '/api/books?page=';
    const initialBookLoadProgress = useApiProgress('get', path);

    let lastBookId = 0;
    let firstBookId = 0;
    if (bookPage.content.length > 0) {
        firstBookId = bookPage.content[0].id;

        const lastBookIndex = bookPage.content.length - 1;
        lastBookId = bookPage.content[lastBookIndex].id;
    }
    const oldBookPath = username ? `/api/books?id=${lastBookId}` : `/api/books?id=${lastBookId}`;
    const loadOldBooksProgress = useApiProgress('get', oldBookPath, true);

    const newBookPath = username
        ? `/api/books?id=${firstBookId}&direction=after`
        : `/api/books?id=${firstBookId}&direction=after`;

    const loadNewBooksProgress = useApiProgress('get', newBookPath, true);

    useEffect(() => {
        const getCount = async () => {
            const response = await getNewBookCount(firstBookId, username);
            setNewBookCount(response.data.count);
        };
        let looper = setInterval(getCount, 5000);
        return function cleanup() {
            clearInterval(looper);
        };
    }, [firstBookId, username]);

    useEffect(() => {
        const loadBooks = async page => {
            try {
                const response = await getBooks(username, page);
                setBookPage(previousBookPage => ({
                    ...response.data,
                    content: [...previousBookPage.content, ...response.data.content]
                }));
            } catch (error) {}
        };
        loadBooks();
    }, [username]);

    const loadOldBooks = async () => {
        const response = await getOldBooks(lastBookId, username);
        setBookPage(previousBookPage => ({
            ...response.data,
            content: [...previousBookPage.content, ...response.data.content]
        }));
    };

    const loadNewBooks = async () => {
        const response = await getNewBooks(firstBookId, username);
        setBookPage(previousBookPage => ({
            ...previousBookPage,
            content: [...response.data, ...previousBookPage.content]
        }));
        setNewBookCount(0);
    };

    const onDeleteBookSuccess = id => {
        setBookPage(previousBookPage => ({
            ...previousBookPage,
            content: previousBookPage.content.filter(book => book.id !== id)
        }));
    };

    const { content, last } = bookPage;

    if (content.length === 0) {
        return <div className="alert alert-secondary text-center">{initialBookLoadProgress ? <Spinner /> : t('There are no books')}</div>;
    }

    return (
        <div>
            {newBookCount > 0 && (
                <div
                    className="alert alert-secondary text-center mb-1"
                    style={{ cursor: loadNewBooksProgress ? 'not-allowed' : 'pointer' }}
                    onClick={loadNewBooksProgress ? () => {} : loadNewBooks}
                >
                    {loadNewBooksProgress ? <Spinner /> : t('There are new books')}
                </div>
            )}
            {content.map(book => {
                return <BookView key={book.id} book={book} onDeleteBook={onDeleteBookSuccess} />;
            })}
            {!last && (
                <div
                    className="alert alert-secondary text-center"
                    style={{ cursor: loadOldBooksProgress ? 'not-allowed' : 'pointer' }}
                    onClick={loadOldBooksProgress ? () => {} : loadOldBooks}
                >
                    {loadOldBooksProgress ? <Spinner /> : t('Load old books')}
                </div>
            )}
        </div>
    );
};

export default BookFeed;