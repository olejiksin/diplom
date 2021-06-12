import React from 'react';
import {Link} from "react-router-dom";
import {connect} from "react-redux";
import {AppState} from "../types/types";
import {exit} from '../actions/actions';
import {DockerList} from "../components/dockerfileList";

interface Props {
    exit: (props: any) => void;
    state: AppState;
}


const HelpPage: React.FC<Props> = props => {

        const {exit, state} = props;

        const doExit = () => {
            exit(props);
        };

        const doScroll = (e: React.SyntheticEvent, id: string) => {
            e.preventDefault();
            //nioch
            let el = document.getElementById(id);
            if (el !== null) {
                let animTime = 300;
                let frames = 20;
                let coorY = el.getBoundingClientRect().top + window.pageYOffset;
                let scroller = setInterval(function () {
                    let scrollBy = parseInt((coorY / frames).toFixed());
                    if (document.documentElement.scrollTop < coorY) {
                        if (document.documentElement.scrollTop + window.innerHeight < document.documentElement.scrollHeight
                            && scrollBy + document.documentElement.scrollTop < coorY) {
                            window.scrollBy(0, scrollBy);
                        } else {
                            window.scrollTo(0, coorY);
                            clearInterval(scroller);
                        }
                    } else {
                        if (document.documentElement.scrollTop - scrollBy > coorY) {
                            window.scrollBy(0, -scrollBy * 3);
                        } else {
                            window.scrollTo(0, coorY);
                            clearInterval(scroller);
                        }
                    }
                }, animTime / frames);
            } else {

            }
        };

        const showHideDockerList = (e: React.SyntheticEvent) => {
            let k = e.currentTarget.children[0] as HTMLElement;
            if (e.currentTarget.children.length === 1) {
                if (e.type !== 'mouseleave') {
                    if (k !== null) {
                        k.style.display = 'block';
                    }
                } else {
                    k.style.display = 'none';
                }
            }
        };

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
            <div>
                {state.currentUser !== 'default' ?
                    <div className={'header'} style={{background: 'white'}}>
                        <Link to={'/help'} className={'btn btn-selected'}>Help</Link>
                        <Link to={`/main/${state.currentUser}`} className={'btn'}>Main page</Link>
                        <Link onClick={() => doExit()} to={'/login'} className={'btn'}>Exit</Link>
                    </div>
                    :
                    <div className={'header'} style={{background: 'white'}}>
                        <Link to={'/help'} className={'btn btn-selected'}>Help</Link>
                        <Link to={'/login'} className={'btn'}>Sign in</Link>
                        <Link to={'/reg'} className={'btn'}>Sign up</Link>
                    </div>
                }
                <div className={'help-menu'}>
                    <li className={'help-menu-li'} onClick={(e) => doScroll(e, 'info')}>Information</li>
                    <li className="help-menu-li" onClick={(e) => doScroll(e, 'deployHelp')}>How to deploy</li>
                    <li className={'help-menu-li'} onClick={(e) => doScroll(e, 'lists')}>Applications</li>
                    <li className={'help-menu-li'} onClick={(e) => doScroll(e, 'db-lists')}>Databases</li>
                    <li className="help-menu-li" onMouseUp={(e) => showHideDockerList(e)}
                        onMouseLeave={(e) => showHideDockerList(e)}>Dockerfiles for application
                        <ul className="help-menu-dfiles">
                            <li className="help-menu-li-ul" onClick={(e) => doScroll(e, 'java')}> Java</li>
                            <li className="help-menu-li-ul" onClick={(e) => doScroll(e, 'python')}>Python</li>
                            <li className="help-menu-li-ul" onClick={(e) => doScroll(e, 'nodejs')}>Node.JS</li>
                            <li className="help-menu-li-ul" onClick={(e) => doScroll(e, 'c#')}>C#</li>
                        </ul>
                    </li>
                </div>
                <div className={'help-main'}>
                    <h1 className="h1-help">Help page</h1>
                    <div className="help-main-div" id="info">
                        <h1>Start information and etc.</h1>
                        <div className="help-main-div-div">
                            DHDFHDFHD
                        </div>
                    </div>
                    <div className="help-main-div" id="deployHelp">
                        <h1>How to deploy?</h1>
                        <div className="help-main-div-div">
                            DHDFHDFHD
                        </div>
                    </div>
                    <div className="help-main-div" id="lists">
                        <h1>Apps list and how to add new</h1>
                        <div className="help-main-div-div">
                            <h2>Monolith</h2>
                          <p>dfgdgdfgdfgdfgdfgdfg</p>
                            <p>fgdfgdfhdfhdfhdhd</p>
                            <h2>Microservice</h2>
                        </div>
                    </div>
                    <div className="help-main-div" id="db-lists">
                        <h1>Database list and how to add new</h1>
                        <div className="help-main-div-div">
                            DHDFHDFHD
                        </div>
                    </div>
                    <DockerList/>
                </div>
                <div className="up-div" onClick={() => moveUp()}/>
            </div>
        );
    }
;
const mapStateToProps = (state: any) => ({
    state: state.appReducer
});

const actions = {
    exit
};

const comp = connect(mapStateToProps, actions)(HelpPage);

export default comp;
