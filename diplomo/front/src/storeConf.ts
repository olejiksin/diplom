import {applyMiddleware, combineReducers, createStore} from "redux";
import {appReducer} from "./reducers/appReducer"
import {composeWithDevTools} from 'redux-devtools-extension';
import thunk, {ThunkMiddleware} from 'redux-thunk';
import logger from 'redux-logger'
import {AppActions} from "./types/actionTypes";


export const rootReducer = combineReducers({
    appReducer: appReducer
});
export type AppState = ReturnType<typeof appReducer>;

export const storeGlob = {...createStore(rootReducer, composeWithDevTools(applyMiddleware(thunk as ThunkMiddleware<AppState, AppActions>, logger)))};

