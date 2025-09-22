import { Box, Button, Container, Flex, Text, useColorMode, useColorModeValue, Image, Center, Heading } from "@chakra-ui/react";
import { IoMoon } from "react-icons/io5";
import { LuSun } from "react-icons/lu";

const Navbar = ({}) => {
	const { colorMode, toggleColorMode } = useColorMode();
	return (
		<Container minW={'4xl'} maxW={'4xl'} align={"center"}>
				<Box px={4} my={4} borderRadius={5} bg={useColorModeValue("#4D4D4D", "#4D4D4D")}>
				<Flex h='32' alignItems={"center"} justifyContent={"space-between"}>
					{/* Left side */}
					<Flex
						alignItems={"center"}
						justifyContent={"center"}
						gap={3}
						display={{ base: "none", sm: "flex" }}
					>
						<Image width={'130px'} height={'120px'} objectFit='contain' src='/Banner_imagem.png' alt='banner'/>
					</Flex>
					<Flex
						alignItems={"center"}
						justifyContent={"center"}
						gap={3}
						display={{ base: "none", sm: "flex" }}					
					>
						<Text color='white' fontWeight={"bold"} fontSize={"30px"} fontFamily={"Verdana, sans-serif"} whiteSpace={"pre-line"}>{"GRUPO DE TEATRO \n BOCA DE CENA - MINDE"}</Text>

					</Flex>
					{/* Right side */}
					<Flex gap={3} alignItems={"center"}>
						<Button onClick={toggleColorMode} backgroundColor={"#4D4D4D"}>
							{colorMode === "light" ? <IoMoon size={20} /> : <LuSun size={20} />}
						</Button>
					</Flex>
				</Flex>
			</Box>
		</Container>
	);
};
export default Navbar;
