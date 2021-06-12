import React from 'react';
import {Link} from "react-router-dom";
import {connect} from "react-redux";
import {App, AppState, AppWithServerData, DataBaseWithServer, MicroAppWithServerData} from "../types/types";
import {
    addNewApp, addNewDB, addNewMicroApp,
    editApp, exit,
    getMainData,
    removeApp, removeDB,
    startOrStopApp, startOrStopDB
} from "../actions/actions";
import {AppList} from "../components/appsList";
import {AppControl} from "../components/appsControl";
import {Loader} from '../components/loader';
import {DbList} from "../components/dbList";
import {DBControl} from "../components/dbControl";
import {storage} from "../reducers/appReducer";


const {useEffect} = React;

interface Props {
    addNewDB: (file: any, db: DataBaseWithServer, login: string) => void;
    getMainData: (login: string) => void;
    addNewApp: (app: AppWithServerData, login: string) => void;
    addNewMicroApp: (app: MicroAppWithServerData, login: string) => void;
    removeApp: (id: string) => void;
    editApp: (app: App) => void;
    startOrStopApp: (id: string) => void;
    exit: (props: any) => void;
    state: AppState;
    removeDB: (id: string) => void;
    startOrStopDB: (id: string) => void;
}

const {useState} = React;

const MainPage: React.FC<Props> = props => {

        const [page, setPage] = useState('1');
        const [isLoading, setIsLoading] = useState(true);
        const [newModal, setNewModal] = useState(false);

        useEffect(() => {
            const {currentUser} = props.state;
            if (storage.username) {
                setTimeout(() => {
                    props.getMainData(currentUser);
                    setIsLoading(false);
                }, 500)
            } else {
                // @ts-ignore
                props.history.push('/login');
            }
        }, []);

        const doEx = () => {
            props.exit(props);
        };
        const EMPTY: any = [];


        const changePage = (e: React.SyntheticEvent, page: string) => {
            setPage(page);
            setNewModal(false);
        }

        const showHideController = (e: React.SyntheticEvent) => {
            setNewModal(true);
        }
        const moveUp = () => {
            let animTime = 300;
            let frames = 20;
            let scrollBy = parseInt((document.documentElement.scrollTop / frames).toFixed());
            let scroller = setInterval(() => {
                if (document.documentElement.scrollTop - scrollBy > 0) {
                    window.scrollBy(0, -scrollBy);
                } else {
                    window.scrollTo(0, 0)
                    clearInterval(scroller);
                }
            }, animTime / frames)
        }

        return (
            <div className="main-main">
                <div className={'header'} style={{background: 'white'}}>
                    <Link to={'/help/'} className={'btn'}>Help</Link>
                    <Link to={`/main/${props.state.currentUser}`} className={'btn btn-selected'}>Main page</Link>
                    <Link to={'/login/'} onClick={() => doEx()} className={'btn'}>Exit</Link>
                </div>
                <div className="header-main">
                    <button className={page === '1' ? 'header-link  header-link-selected' : 'header-link'}
                            onClick={(e) => changePage(e, '1')}>App
                        list
                    </button>
                    <button className={page === '2' ? 'header-link header-link-selected' : 'header-link'}
                            onClick={(e) => changePage(e, '2')}>Database list
                    </button>
                    <button className={newModal === true ? 'header-link header-link-selected' : 'header-link'}
                            onClick={(e) => showHideController(e)}>{page === '1' ? 'Add new app' : 'Add new db'}</button>
                </div>
                {isLoading !== true ?
                    page === '1' ?
                        <div>
                            {newModal === true ?
                                <AppControl deployStatus={props.state.deployStatus} addNewApp={props.addNewApp}
                                            addNewMicroApp={props.addNewMicroApp}
                                            currentUser={props.state.currentUser}/>
                                :
                                <AppList microservices={props.state.profile.microservices || EMPTY}
                                         apps={props.state.profile.appList || EMPTY} editApp={props.editApp}
                                         removeApp={props.removeApp}
                                         startOrStopApp={props.startOrStopApp}/>
                            }
                        </div>
                        :
                        <div>
                            {newModal === true ?
                                <DBControl currentUser={props.state.currentUser} addNewDB={props.addNewDB}/> :
                                <DbList startOrStopDB={props.startOrStopDB}
                                        databases={props.state.profile.databases || EMPTY} removeDB={props.removeDB}/>
                            }
                        </div>
                    :
                    <Loader/>}
                <div className="up-div" onClick={() => moveUp()}/>
            </div>
        );
    }
;


const mapStateToProps = (state: any) => (
    {
        state: state.appReducer
    }
);


const actions = {
        getMainData,
        addNewDB,
        addNewApp,
        removeApp,
        editApp,
        exit,
        startOrStopApp,
        removeDB,
        startOrStopDB,
        addNewMicroApp
    }
;

export default connect(mapStateToProps, actions)(MainPage);
