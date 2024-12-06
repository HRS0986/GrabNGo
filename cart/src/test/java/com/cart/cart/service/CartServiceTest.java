package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.exception.ResourceNotFoundException;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ModelMapper modelMapper;

    private Cart cart;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cart = new Cart();
        cart.setCartId(1);
        cart.setUserId(100);
        cart.setTotalAmount(5);
        cart.setTotalPrice(250.0);
        cart.setActive(true);

        cartDTO = new CartDTO();
        cartDTO.setCartId(1);
        cartDTO.setUserId(100);
        cartDTO.setTotalAmount(5);
        cartDTO.setTotalPrice(250.0);
        cartDTO.setActive(true);
    }

    @Test
    void getAllCarts_shouldReturnAllCarts() {
        List<Cart> cartList = List.of(cart);
        List<CartDTO> cartDTOList = List.of(cartDTO);

        when(cartRepo.findAll()).thenReturn(cartList);
        when(modelMapper.map(cartList, new TypeToken<List<CartDTO>>() {}.getType())).thenReturn(cartDTOList);

        List<CartDTO> result = cartService.getAllCarts();

        assertNotNull(result);
        assertEquals(cartDTOList.size(), result.size());
        assertEquals(cartDTOList.get(0).getCartId(), result.get(0).getCartId());

        verify(cartRepo, times(1)).findAll();
    }

    @Test
    void getCartByUserId_shouldReturnCartWhenFound() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.of(cart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartByUserId(100);

        assertNotNull(result);
        assertEquals(cartDTO.getCartId(), result.getCartId());
        verify(cartRepo, times(1)).findByUserId(100);
    }

    @Test
    void getCartByUserId_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.getCartByUserId(100));

        assertEquals("Cart not found with the User ID 100", exception.getMessage());
    }

    @Test
    void createCart_shouldCreateNewCartWhenNotExists() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(100);

        assertNotNull(result);
        assertEquals(cartDTO.getUserId(), result.getUserId());
        verify(cartRepo, times(1)).save(any(Cart.class));
    }

    @Test
    void createCart_shouldReturnExistingCartWhenExists() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.of(cart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(100);

        assertNotNull(result);
        assertEquals(cartDTO.getCartId(), result.getCartId());
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void softDeleteCart_shouldSetCartToInactive() {
        when(cartRepo.findById(1)).thenReturn(Optional.of(cart));

        cartService.softDeleteCart(1);

        verify(cartRepo, times(1)).save(cart);
        assertFalse(cart.isActive());
    }

    @Test
    void softDeleteCart_shouldDoNothingWhenCartNotFound() {
        when(cartRepo.findById(1)).thenReturn(Optional.empty());

        cartService.softDeleteCart(1);

        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void deleteByUserId_shouldSetCartToInactive() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.of(cart));

        cartService.deleteByUserId(100);

        verify(cartRepo, times(1)).save(cart);
        assertFalse(cart.isActive());
    }

    @Test
    void deleteByUserId_shouldDoNothingWhenCartNotFound() {
        when(cartRepo.findByUserId(100)).thenReturn(Optional.empty());

        cartService.deleteByUserId(100);

        verify(cartRepo, never()).save(any(Cart.class));
    }
}
