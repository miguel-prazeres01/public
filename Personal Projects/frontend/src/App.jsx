import { Container, Stack, Text, Image, Flex, Button, Box, Heading } from "@chakra-ui/react";
import Navbar from "./components/Navbar";
import SeatGrid from "./components/SeatGrid";
import { useState } from "react";
import BlogContainer from "./components/BlogContainer";
import ReserveSeatsButton from "./components/ReserveSeatsButton";

import sha256 from 'crypto-js/sha256';
import hmac from 'crypto-js/hmac-sha256';
import Base64 from 'crypto-js/enc-base64';
import Show from "./pages/Show";
import Home from "./pages/Home";

import { BrowserRouter, Routes, Route} from "react-router-dom";


export const BASE_URL = import.meta.env.VITE_BASE_URL;
export const SECRET=import.meta.env.VITE_SECRET;

export function signPayload(payload, secret) {
	return Base64.stringify(hmac(payload, secret));
}

function App() {
	return (
		//<Tickets />
		//<Home />
		<BrowserRouter>
			<Routes>
				<Route path="/" element={ <Home/>}/>
				<Route path="/show/:showId" element={<Show/>} />
			</Routes>
		</BrowserRouter>
		
	);
}

export default App;