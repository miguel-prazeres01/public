import { Flex, Grid, Spinner, Text, Box, Heading } from "@chakra-ui/react";

import SeatButton from "./SeatButton";
import { useEffect, useState } from "react";
import { BASE_URL } from "../App";
import { SEATS } from "../dummy/dummy";
import {SECRET} from "../App";
import { signPayload } from "../App";
import { useParams } from "react-router";

const SeatGrid = ({seats, setSeats, spot, setPrice, reserved, setReserved, reservedServer, refreshTrigger }) => {
	const [isLoading, setIsLoading] = useState(true);
	const urlParams = useParams();

	useEffect(() => {
		const getSeats = async () => {
			try {
				const payload = JSON.stringify({ "showId": urlParams.showId});
				const signature = signPayload(payload, SECRET);
				const res = await fetch(BASE_URL + "/shows/allshows", {
					method: "POST",
					headers: {
					"Content-Type": "application/json",
					"X-Signature": signature,
				},
				body: JSON.stringify({"showId": urlParams.showId}),
				});
				const data = await res.json();

				if (!res.ok) {
					throw new Error(data.error);
				}
				setSeats(data["show"]["seats"]);
				setPrice(data["show"]["price"]);
			} catch (error) {
				console.error(error);
			} finally {
				setIsLoading(false);
			}
		};
		getSeats();
	}, [setSeats, refreshTrigger]);
	
	return (
		<>
			<Grid
				templateColumns={{
					base: "repeat(16, 1fr)",
					md: "repeat(16, 1fr)",
					lg: "repeat(16, 1fr)",
				}}
				gap={3}
			>
				{seats.map((seat) => (seat.seatType === spot ?
					<SeatButton key={seat.id} seat={seat} setSeats={setSeats} reserved={reserved} setReserved={setReserved} reservedServer={reservedServer}/> : null
				))}
			</Grid>

			{isLoading && (
				<Flex justifyContent={"center"}>
					<Spinner size={"xl"} />
				</Flex>
			)}
		</>
	);
};
export default SeatGrid;
